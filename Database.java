import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Database {
    private final File dbFile;
    private final Map<String, Record> recordMap = new HashMap<>();

    public Database(String dbFileName) throws IOException, ClassNotFoundException {
        this.dbFile = new File(dbFileName);
        if (dbFile.exists()) {
            for (Record record : getAllRecordsFromFile()) {
                recordMap.put(record.getId(), record);
            }
        }
    }

    public void addRecord(Record record) throws IOException {
        if (recordMap.containsKey(record.getId())) {
            throw new IllegalArgumentException("Record with this ID already exists");
        }
        recordMap.put(record.getId(), record);
        saveAllRecordsToFile();
    }

    public List<Record> getAllRecordsFromFile() throws IOException, ClassNotFoundException {
        List<Record> records = new ArrayList<>();
        if (!dbFile.exists()) {
            return records;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(dbFile))) {
            while (true) {
                try {
                    Record record = (Record) ois.readObject();
                    records.add(record);
                } catch (IOException | ClassNotFoundException ex) {
                    break;
                }
            }
        }
        return records;
    }

    public void deleteRecordsByField(String field, String value) throws IOException {
        recordMap.values().removeIf(record -> {
            return switch (field) {
                case "id" -> record.getId().equals(value);
                case "name" -> record.getName().equals(value);
                case "age" -> Integer.toString(record.getAge()).equals(value);
                case "address" -> record.getAddress().equals(value);
                default -> false;
            };
        });
        saveAllRecordsToFile();
    }

    public List<Record> searchByField(String field, String value) {
        return recordMap.values().stream()
                .filter(record -> {
                    return switch (field) {
                        case "id" -> record.getId().equals(value);
                        case "name" -> record.getName().equals(value);
                        case "age" -> Integer.toString(record.getAge()).equals(value);
                        case "address" -> record.getAddress().equals(value);
                        default -> false;
                    };
                })
                .toList();
    }

    public void saveAllRecordsToFile() throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(dbFile))) {
            for (Record record : recordMap.values()) {
                oos.writeObject(record);
            }
        }
    }

    public boolean isUnique(String id) throws IOException, ClassNotFoundException {
        for (Record record : getAllRecordsFromFile()) {
            if (record.getId().equals(id)) {
                return false;
            }
        }
        return true;
    }

    public void backup(String backupFileName) throws IOException {
        File backupFile = new File(backupFileName);
        try (InputStream is = new FileInputStream(dbFile);
             OutputStream os = new FileOutputStream(backupFile)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        }
    }

    public void restoreFromBackup(String backupFileName) throws IOException {
        File backupFile = new File(backupFileName);
        try (InputStream is = new FileInputStream(backupFile);
             OutputStream os = new FileOutputStream(dbFile)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        }
    }
}
