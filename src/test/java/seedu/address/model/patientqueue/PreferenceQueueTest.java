package seedu.address.model.patientqueue;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static seedu.address.testutil.TypicalPersons.ALICE;

import org.junit.Test;

import seedu.address.model.ModelManager;

public class PreferenceQueueTest {
    private ModelManager modelManager = new ModelManager();
    @Test
    public void hasPatient_patientNotInPreferenceQueue_returnsFalse() {
        assertFalse(modelManager.hasPatientInMainQueue()); // using main model's main queue
        // rather than preference queue
    }
    @Test
    public void hasPatient_patientInPreferenceQueue_returnTrue() {
        modelManager.enqueue(ALICE);
        assertTrue(modelManager.hasPatientInMainQueue());
    }
}
