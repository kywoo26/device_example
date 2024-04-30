import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class DeviceDriverTest {

    private static final byte EMPTY = (byte) 0xFF;
    private static final int BASE_ADDRESS = 0x00;

    @Mock
    private FlashMemoryDevice flashMemory;

    private DeviceDriver deviceDriver;

    @BeforeEach
    void setUp() {
        deviceDriver = new DeviceDriver(flashMemory);
    }

    @Test
    void testReadConsistentData() {
        doReturn(EMPTY).when(flashMemory).read(BASE_ADDRESS);
        byte data = deviceDriver.read(BASE_ADDRESS);

        verify(flashMemory, times(DeviceDriver.ITERATION_FACTOR)).read(BASE_ADDRESS);
        assertThat(data).isEqualTo(EMPTY);
    }

    @Test
    void testReadInconsistentData() {
        doReturn((byte) 0x01, (byte) 0x02).when(flashMemory).read(BASE_ADDRESS);

        assertThatThrownBy(() -> deviceDriver.read(BASE_ADDRESS))
                .isInstanceOf(ReadFailException.class)
                .hasMessage("Failed to read");
    }
}