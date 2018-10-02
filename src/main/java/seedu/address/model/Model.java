package seedu.address.model;

import java.util.function.Predicate;

import javafx.collections.ObservableList;
import seedu.address.model.doctor.Doctor;
import seedu.address.model.person.Person;

/**
 * The API of the Model component.
 */
public interface Model {
    /** {@code Predicate} that always evaluate to true */
    Predicate<Person> PREDICATE_SHOW_ALL_PERSONS = unused -> true;

    /** {@code Predicate} that always evaluate to true */
    Predicate<Doctor> PREDICATE_SHOW_ALL_DOCTORS = unused -> true;

    /** Clears existing backing model and replaces with the provided new data. */
    void resetData(ReadOnlyAddressBook newData);

    /** Returns the AddressBook */
    ReadOnlyAddressBook getAddressBook();

    /**
     * Returns true if a person with the same identity as {@code person} exists in the address book.
     */
    boolean hasPerson(Person person);

    /**
     * Returns true if a doctor with the same identity as {@code doctor} exists in the address book.
     */
    boolean hasDoctor(Doctor doctor);

    /**
     * Deletes the given person.
     * The person must exist in the address book.
     */
    void deletePerson(Person target);

    /**
     * Deletes the given doctor.
     * The doctor must exist in the address book.
     */
    void deleteDoctor(Doctor doctor);

    /**
     * Adds the given person.
     * {@code person} must not already exist in the address book.
     */
    void addPerson(Person person);

    /**
     * Adds the given doctor.
     * {@code doctor} must not already exist in the address book.
     */
    void addDoctor(Doctor doctor);

    /**
     * Replaces the given person {@code target} with {@code editedPerson}.
     * {@code target} must exist in the address book.
     * The person identity of {@code editedPerson} must not be the same as another existing person in the address book.
     */
    void updatePerson(Person target, Person editedPerson);

    /**
     * Replaces the given doctor {@code target} with {@code editedDoctor}.
     * {@code target} must exist in the address book.
     * The doctor identity of {@code editedDoctor} must not be the same as another existing doctor in the address book.
     */
    void updateDoctor(Doctor target, Doctor editedDoctor);

    /** Returns an unmodifiable view of the filtered person list */
    ObservableList<Person> getFilteredPersonList();

    /** Returns an unmodifiable view of the filtered doctor list */
    ObservableList<Doctor> getFilteredDoctorList();

    /**
     * Updates the filter of the filtered person list to filter by the given {@code predicate}.
     * @throws NullPointerException if {@code predicate} is null.
     */
    void updateFilteredPersonList(Predicate<Person> predicate);

    /**
     * Updates the filter of the filtered doctor list to filter by the given {@code predicate}.
     * @throws NullPointerException if {@code predicate} is null.
     */
    void updateFilteredDoctorList(Predicate<Doctor> predicate);

    /**
     * Returns true if the model has previous address book states to restore.
     */
    boolean canUndoAddressBook();

    /**
     * Returns true if the model has undone address book states to restore.
     */
    boolean canRedoAddressBook();

    /**
     * Restores the model's address book to its previous state.
     */
    void undoAddressBook();

    /**
     * Restores the model's address book to its previously undone state.
     */
    void redoAddressBook();

    /**
     * Saves the current address book state for undo/redo.
     */
    void commitAddressBook();

    //@@author iamjackslayer
    /**
     * Enqueues the given person.
     * TODO Change Person object to Patient Object
     */
    void enqueue(Person patient);

    //@@author iamjackslayer
    /**
     * Enqueues the given person into preference queue.
     * TODO Change Person object to Patient Object
     */
    void enqueueIntoPreferenceQueue(Person patient);

    //@@author iamjackslayer
    /**
     * Check if patient exists in the patient queue.
     */
    boolean hasPatientInMainQueue();

    //@@author iamjackslayer
    /**
     * Check if patient exists in the patient queue.
     */
    boolean hasPatientInPreferenceQueue();
}
