package at.rgstoettner.alexahome.plugin.v2;

public interface LockState {


    /**
     * “Alexa, is lock name locked/unlocked?”
     *
     * @return
     * Indicates the locked state of the specified appliance. Valid values are LOCKED, UNLOCKED
     */
    String getLockState();


    /**
     * “Alexa, lock the lock name”
     *
     * @param lockState
     * Indicates the requested lock-state of the specified appliance. Valid value for this request is LOCKED.
     * @return
     * Indicates the locked state of the specified appliance. Valid value for this directive is LOCKED or UNLOCKED.
     */
    String setLockState(String lockState);
}
