import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class DeviceDriverTest {

    private static final byte EMPTY = (byte) 0xFF;
    private static final byte NOT_EMPTY = (byte) 0xFE;
    private static final int BASE_ADDRESS = 0x00;

    @Mock
    private FlashMemoryDevice flashMemory;

    private DeviceDriver deviceDriver;

    @BeforeEach
    void setUp() {
        deviceDriver = new DeviceDriver(flashMemory);
    }

    @Test
    void testReadSuccess() {
        doReturn(EMPTY).when(flashMemory).read(BASE_ADDRESS);
        byte data = deviceDriver.read(BASE_ADDRESS);

        verify(flashMemory, times(DeviceDriver.ITERATION_FACTOR)).read(BASE_ADDRESS);
        assertThat(data).isEqualTo(EMPTY);
    }

    @Test
    void testReadFail() {
        doReturn((byte) 0x01, (byte) 0x02).when(flashMemory).read(BASE_ADDRESS);

        assertThatThrownBy(() -> deviceDriver.read(BASE_ADDRESS))
                .isInstanceOf(ReadFailException.class)
                .hasMessage("Failed to read");
    }

    @Test
    void testWriteSuccess() {
        byte data = (byte) 0x01;
        doReturn(EMPTY).when(flashMemory).read(BASE_ADDRESS);
        doNothing().when(flashMemory).write(BASE_ADDRESS, data);

        deviceDriver.write(BASE_ADDRESS, data);

        verify(flashMemory, times(5)).read(BASE_ADDRESS);
        verify(flashMemory).write(BASE_ADDRESS, data);
    }

    @Test
    void testWriteFail() {
        byte data = (byte) 0x01;
        doReturn(NOT_EMPTY).when(flashMemory).read(BASE_ADDRESS);

        assertThatThrownBy(() -> deviceDriver.write(BASE_ADDRESS, data))
                .isInstanceOf(WriteFailException.class)
                .hasMessage("Failed to write");
    }
}