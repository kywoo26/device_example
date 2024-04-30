import org.assertj.core.util.VisibleForTesting;

/**
 * This class is used by the operating system to interact with the hardware 'FlashMemoryDevice'.
 */
public class DeviceDriver {
    public static final int ITERATION_FACTOR = 5;
    private static final byte[] buffer = new byte[ITERATION_FACTOR];

    private final FlashMemoryDevice flashMemory;

    public DeviceDriver(FlashMemoryDevice flashMemory) {
        this.flashMemory = flashMemory;
    }

    public byte read(long address) throws ReadFailException {
        readToBuffer(address);

        if (!isConsistentBuffer()) {
            throw new ReadFailException("Failed to read");
        }

        return buffer[0];
    }

    private void readToBuffer(long address) {
        for (int i = 0; i < ITERATION_FACTOR; i++) {
            buffer[i] = flashMemory.read(address);
        }
    }

    private boolean isConsistentBuffer() {
        for (int i = 1; i < ITERATION_FACTOR; i++) {
            if (buffer[i - 1] != buffer[i]) {
                return false;
            }
        }
        return true;
    }

    public void write(long address, byte data) {
        // TODO: implement this method
    }
}