package seedu.address.storage;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.logging.Logger;

import com.google.common.eventbus.Subscribe;

import seedu.address.commons.core.ComponentManager;
import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.events.model.ClinicIoChangedEvent;
import seedu.address.commons.events.storage.DataSavingExceptionEvent;
import seedu.address.commons.exceptions.DataConversionException;
import seedu.address.model.ReadOnlyClinicIo;
import seedu.address.model.UserPrefs;

/**
 * Manages storage of ClinicIo data in local storage.
 */
public class StorageManager extends ComponentManager implements Storage {

    private static final Logger logger = LogsCenter.getLogger(StorageManager.class);
    private ClinicIoStorage clinicIoStorage;
    private UserPrefsStorage userPrefsStorage;


    public StorageManager(ClinicIoStorage clinicIoStorage, UserPrefsStorage userPrefsStorage) {
        super();
        this.clinicIoStorage = clinicIoStorage;
        this.userPrefsStorage = userPrefsStorage;
    }

    // ================ UserPrefs methods ==============================

    @Override
    public Path getUserPrefsFilePath() {
        return userPrefsStorage.getUserPrefsFilePath();
    }

    @Override
    public Optional<UserPrefs> readUserPrefs() throws DataConversionException, IOException {
        return userPrefsStorage.readUserPrefs();
    }

    @Override
    public void saveUserPrefs(UserPrefs userPrefs) throws IOException {
        userPrefsStorage.saveUserPrefs(userPrefs);
    }


    // ================ ClinicIo methods ==============================

    @Override
    public Path getAddressBookFilePath() {
        return clinicIoStorage.getAddressBookFilePath();
    }

    @Override
    public Optional<ReadOnlyClinicIo> readAddressBook() throws DataConversionException, IOException {
        return readAddressBook(clinicIoStorage.getAddressBookFilePath());
    }

    @Override
    public Optional<ReadOnlyClinicIo> readAddressBook(Path filePath) throws DataConversionException, IOException {
        logger.fine("Attempting to read data from file: " + filePath);
        return clinicIoStorage.readAddressBook(filePath);
    }

    @Override
    public void saveAddressBook(ReadOnlyClinicIo addressBook) throws IOException {
        saveAddressBook(addressBook, clinicIoStorage.getAddressBookFilePath());
    }

    @Override
    public void saveAddressBook(ReadOnlyClinicIo addressBook, Path filePath) throws IOException {
        logger.fine("Attempting to write to data file: " + filePath);
        clinicIoStorage.saveAddressBook(addressBook, filePath);
    }


    @Override
    @Subscribe
    public void handleAddressBookChangedEvent(ClinicIoChangedEvent event) {
        logger.info(LogsCenter.getEventHandlingLogMessage(event, "Local data changed, saving to file"));
        try {
            saveAddressBook(event.data);
        } catch (IOException e) {
            raise(new DataSavingExceptionEvent(e));
        }
    }

}
