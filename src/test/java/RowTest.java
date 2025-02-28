import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class RowTest {

    @Test
    void printTest() {
        Row row = new Row();
        PrintService mockPrintService = mock(PrintService.class);
        row.setPrintService(mockPrintService);

        row.print();

        verify(mockPrintService).printRow(anyString());
    }
}