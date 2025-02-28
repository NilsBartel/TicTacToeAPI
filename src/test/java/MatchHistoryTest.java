import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class MatchHistoryTest {


    @Test
    void test() {
        PrintService mockPrintService = Mockito.mock(PrintService.class);
        Match mockMatch = Mockito.mock(Match.class);
        List<Match> matches = new ArrayList<>();
        int times = 10;
        for(int i = 0;i < times;i++) {
            matches.add(mockMatch);
        }

        MatchHistory.printMatchHistory(matches, mockPrintService);
        verify(mockPrintService, times(times)).printBoardNr(anyInt());
        verify(mockPrintService, times(times)).printBoard(any());
        verify(mockPrintService, times(times)).printSecondsElapsed(any(), any());
        verify(mockPrintService, times(times)).printDate(any());
    }

}