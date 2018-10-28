package seedu.address.model;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;
import static seedu.address.model.analytics.StatisticType.APPOINTMENT;
import static seedu.address.model.analytics.StatisticType.DOCTOR;

import java.util.Map;
import java.util.function.Predicate;
import java.util.logging.Logger;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;

import seedu.address.commons.core.ComponentManager;
import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.events.model.AddressBookChangedEvent;
import seedu.address.commons.events.model.AnalyticsDisplayEvent;
import seedu.address.model.analytics.Analytics;
import seedu.address.model.analytics.StatData;
import seedu.address.model.analytics.StatisticType;
import seedu.address.model.appointment.Appointment;
import seedu.address.model.consultation.Consultation;
import seedu.address.model.doctor.Doctor;
import seedu.address.model.patientqueue.MainQueue;
import seedu.address.model.patientqueue.PreferenceQueue;
import seedu.address.model.person.Person;

/**
 * Represents the in-memory model of the address book data.
 */
public class ModelManager extends ComponentManager implements Model {
    private static final Logger logger = LogsCenter.getLogger(ModelManager.class);

    private final VersionedAddressBook versionedAddressBook;
    private final FilteredList<Person> filteredPersons;
    private final FilteredList<Doctor> filteredDoctors;
    private final FilteredList<Appointment> filteredAppointments;
    private final FilteredList<Consultation> filteredConsultations;
    private final MainQueue mainQueue;
    private final PreferenceQueue preferenceQueue;
    private final Analytics analytics;

    /**
     * Initializes a ModelManager with the given addressBook and userPrefs.
     */
    public ModelManager(ReadOnlyAddressBook addressBook, UserPrefs userPrefs) {
        super();
        requireAllNonNull(addressBook, userPrefs);

        logger.fine("Initializing with address book: " + addressBook + " and user prefs " + userPrefs);

        versionedAddressBook = new VersionedAddressBook(addressBook);
        //@@author jjlee050
        filteredPersons = new FilteredList<>(versionedAddressBook.getPersonList());
        filteredDoctors = new FilteredList<>(versionedAddressBook.getDoctorList());
        filteredAppointments = new FilteredList<>(versionedAddressBook.getAppointmentList());
        filteredConsultations = new FilteredList<>(versionedAddressBook.getConsultationList());
        //@@author iamjackslayer
        mainQueue = new MainQueue();
        preferenceQueue = new PreferenceQueue();
        //@@author arsalanc-v2
        analytics = new Analytics();
    }

    public ModelManager() {
        this(new AddressBook(), new UserPrefs());
    }

    @Override
    public void resetData(ReadOnlyAddressBook newData) {
        versionedAddressBook.resetData(newData);
        indicateAddressBookChanged();
    }

    @Override
    public ReadOnlyAddressBook getAddressBook() {
        return versionedAddressBook;
    }

    /** Raises an event to indicate the model has changed */
    private void indicateAddressBookChanged() {
        raise(new AddressBookChangedEvent(versionedAddressBook));
    }

    //========== Boolean check ===============================================================================

    @Override
    public boolean hasPerson(Person person) {
        requireNonNull(person);
        return versionedAddressBook.hasPerson(person);
    }

    //@@author jjlee050
    @Override
    public boolean hasDoctor(Doctor doctor) {
        requireNonNull(doctor);
        return versionedAddressBook.hasDoctor(doctor);
    }

    @Override
    public boolean hasPatientInMainQueue() {
        return mainQueue.hasPatient();
    }

    @Override
    public boolean hasPatientInPreferenceQueue() {
        return preferenceQueue.hasPatient();
    }

    @Override
    public boolean hasPatientInPatientQueue() {
        boolean hasPatient = hasPatientInPreferenceQueue() || hasPatientInMainQueue();
        return hasPatient;
    }

    //@@author gingivitiss
    @Override
    public boolean hasAppointment(Appointment appt) {
        requireNonNull(appt);
        return versionedAddressBook.hasAppointment(appt);
    }

    @Override
    public boolean hasAppointmentClash(Appointment appt) {
        requireNonNull(appt);
        return versionedAddressBook.hasAppointmentClash(appt);
    }

    //@@author arsalanc-v2
    @Override
    public boolean hasConsultation(Consultation consultation) {
        requireNonNull(consultation);
        return versionedAddressBook.hasConsultation(consultation);
    }
    //========== Delete ======================================================================================

    @Override
    public void deletePerson(Person target) {
        versionedAddressBook.removePerson(target);
        indicateAddressBookChanged();
    }

    //@@author jjlee050
    @Override
    public void deleteDoctor(Doctor target) {
        versionedAddressBook.removeDoctor(target);
        indicateAddressBookChanged();
    }

    //@@author gingivitiss
    @Override
    public void deleteAppointment(Appointment target) {
        versionedAddressBook.removeAppointment(target);
        indicateAddressBookChanged();
    }

    @Override
    public void cancelAppointment(Appointment target) {
        versionedAddressBook.cancelAppointment(target);
        indicateAddressBookChanged();
    }

    //@@author arsalanc-v2
    @Override
    public void deleteConsultation(Consultation target) {
        versionedAddressBook.removeConsultation(target);
    }
    //========== Add =========================================================================================

    @Override
    public void addPerson(Person person) {
        versionedAddressBook.addPerson(person);
        updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        indicateAddressBookChanged();
    }

    //@@author jjlee050
    @Override
    public void addDoctor(Doctor doctor) {
        versionedAddressBook.addDoctor(doctor);
        updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        indicateAddressBookChanged();
    }

    //@@author gingivitiss
    @Override
    public void addAppointment(Appointment appt) {
        versionedAddressBook.addAppointment(appt);
        updateFilteredAppointmentList(PREDICATE_SHOW_ALL_APPOINTMENTS);
    }

    //@author arsalanc-v2
    @Override
    public void addConsultation(Consultation consultation) {
        versionedAddressBook.add(consultation);
        updateFilteredConsultationList(PREDICATE_SHOW_ALL_CONSULTATIONS);
    }
    //========== Update ======================================================================================

    @Override
    public void enqueue(Person patient) {
        mainQueue.add(patient);
    }

    /**
     * Enqueues patient who is consulting a particular doctor into the 'special' queue.
     * @param patient
     */
    @Override
    public void enqueueIntoPreferenceQueue(Person patient) {
        preferenceQueue.add(patient);
    }

    @Override
    public void updatePerson(Person target, Person editedPerson) {
        requireAllNonNull(target, editedPerson);
        versionedAddressBook.updatePerson(target, editedPerson);
        indicateAddressBookChanged();
    }

    //@@author jjlee050
    @Override
    public void updateDoctor(Doctor target, Doctor editedDoctor) {
        requireAllNonNull(target, editedDoctor);
        versionedAddressBook.updateDoctor(target, editedDoctor);
        indicateAddressBookChanged();
    }

    //@@author gingivitiss
    @Override
    public void updateAppointment(Appointment target, Appointment editedAppt) {
        requireAllNonNull(target, editedAppt);
        versionedAddressBook.updateAppointment(target, editedAppt);
        indicateAddressBookChanged();
    }

    //@@author arsalanc-v2
    @Override
    public void updateConsultation(Consultation target, Consultation editedConsultation) {
        requireAllNonNull(target, editedConsultation);
        versionedAddressBook.updateConsultation(target, editedConsultation);
    }
    //=========== Filtered Person List Accessors =============================================================

    /**
     * Returns an unmodifiable view of the list of {@code Person} backed by the internal list of
     * {@code versionedAddressBook}
     */
    @Override
    public ObservableList<Person> getFilteredPersonList() {
        return FXCollections.unmodifiableObservableList(filteredPersons);
    }

    @Override
    public void updateFilteredPersonList(Predicate<Person> predicate) {
        requireNonNull(predicate);
        filteredPersons.setPredicate(predicate);
    }
    //=========== Filtered Doctor List Accessors =============================================================

    //@@author jjlee050
    /**
     * Returns an unmodifiable view of the list of {@code Doctor} backed by the internal list of
     * {@code versionedAddressBook}
     */
    @Override
    public ObservableList<Doctor> getFilteredDoctorList() {
        return FXCollections.unmodifiableObservableList(filteredDoctors);
    }

    //@@author jjlee050
    @Override
    public void updateFilteredDoctorList(Predicate<Doctor> predicate) {
        requireNonNull(predicate);
        filteredDoctors.setPredicate(predicate);
    }

    //=========== Filtered Appointment List Accessors ========================================================

    //@@author gingivitiss
    /**
     * Returns an unmodifiable view of the list of {@code Appointment} backed by the internal list of
     * {@code versionedAddressBook}
     */
    @Override
    public ObservableList<Appointment> getFilteredAppointmentList() {
        return FXCollections.unmodifiableObservableList(filteredAppointments);
    }

    //@@author gingivitiss
    @Override
    public void updateFilteredAppointmentList(Predicate<Appointment> predicate) {
        requireNonNull(predicate);
        filteredAppointments.setPredicate(predicate);
    }

    //=========== Filtered Consultation List Accessors ========================================================
    //@@author arsalanc-v2
    /**
     * Returns an unmodifiable view of the list of {@code Consultation} backed by the internal list of
     * {@code versionedAddressBook}
     */
    @Override
    public ObservableList<Consultation> getFilteredConsultationList() {
        return FXCollections.unmodifiableObservableList(filteredConsultations);
    }

    //@@author arsalanc-v2
    @Override
    public void updateFilteredConsultationList(Predicate<Consultation> predicate) {
        requireNonNull(predicate);
        filteredConsultations.setPredicate(predicate);
    }
    //=========== Undo/Redo ==================================================================================

    @Override
    public boolean canUndoAddressBook() {
        return versionedAddressBook.canUndo();
    }

    @Override
    public boolean canRedoAddressBook() {
        return versionedAddressBook.canRedo();
    }

    @Override
    public void undoAddressBook() {
        versionedAddressBook.undo();
        indicateAddressBookChanged();
    }

    @Override
    public void redoAddressBook() {
        versionedAddressBook.redo();
        indicateAddressBookChanged();
    }

    @Override
    public void commitAddressBook() {
        versionedAddressBook.commit();
    }

    //=========== Analytics ==================================================================================
    //@@author arsalanc-v2

    /**
     *
     */
    @Override
    public void requestAnalyticsDisplay(StatisticType type) {
        raise(new AnalyticsDisplayEvent(type, retrieveAnalytics(type)));
    }

    /**
     *
     */
    public StatData retrieveAnalytics(StatisticType type) {
        updateAnalytics(type);
        return analytics.getAllStatisticsOfType(type);
    }

    /**
     *
     */
    public void updateAnalytics(StatisticType type) {
        analytics.setConsultations(versionedAddressBook.getConsultationList());
        switch (type) {
        case APPOINTMENT:
            analytics.setAppointments(versionedAddressBook.getAppointmentList());
            break;

        case DOCTOR:
            analytics.setDoctors(versionedAddressBook.getDoctorList());
            break;

        default:
            analytics.setAppointments(versionedAddressBook.getAppointmentList());
            break;
        }
    }
    //========================================================================================================

    @Override
    public boolean equals(Object obj) {
        // short circuit if same object
        if (obj == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(obj instanceof ModelManager)) {
            return false;
        }

        // state check
        ModelManager other = (ModelManager) obj;
        //@@author jjlee050
        return versionedAddressBook.equals(other.versionedAddressBook)
                && filteredPersons.equals(other.filteredPersons)
                && filteredDoctors.equals(other.filteredDoctors)
                && filteredAppointments.equals(other.filteredAppointments);
    }
}
