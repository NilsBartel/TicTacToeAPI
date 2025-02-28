//import org.junit.jupiter.api.Test;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//
//class StartGameUtilTest {
//
//
//
//
//        private MatchHistory createMatchHistoryFor_StartWithRunningMatchTest() {
//            MatchHistory matchHistory = new MatchHistory();
//            Match match = new Match();
//
//            match.setSymbol(0, 0, Match.PLAYER_SYMBOL);
//            match.setSymbol(1, 1, Match.PLAYER_SYMBOL);
//            match.setSymbol(0, 1, Match.COMPUTER_SYMBOL);
//            match.setSymbol(2, 2, Match.COMPUTER_SYMBOL);
//
//            match.setIsPlayerTurn(true);
//            match.setStartTimeOLD(1727709534293L);
//            match.setEndTimeOLD(0);
//            match.setDifficulty(DifficultyState.EASY);
//            match.setStatus(MatchStatus.RUNNING);
//
//            matchHistory.addMatch(match);
//
//            return matchHistory;
//        }
//        @Test
//        void startWithRunningMatchTest() {
//
//            MatchHistory matchHistory = createMatchHistoryFor_StartWithRunningMatchTest();
//            DifficultyState difficultyState = DifficultyState.EASY;
//            FileWriteRead mockFileWriteRead = mock(FileWriteRead.class);
//
//            Match match = StartGameUtil.returnRunningOrNewMatchOLD(matchHistory, difficultyState, mockFileWriteRead);
//
//            assertEquals(match, matchHistory.getMatches().getLast());
//        }
//
//
//        private MatchHistory createMatchHistoryFor_startWithEndedMatchTest() {
//            MatchHistory matchHistory = new MatchHistory();
//            Match match = new Match();
//
//            match.setSymbol(0, 0, Match.PLAYER_SYMBOL);
//            match.setSymbol(1, 1, Match.PLAYER_SYMBOL);
//            match.setSymbol(0, 1, Match.COMPUTER_SYMBOL);
//            match.setSymbol(2, 2, Match.COMPUTER_SYMBOL);
//
//            match.setIsPlayerTurn(true);
//            match.setStartTimeOLD(1727709534293L);
//            match.setEndTimeOLD(0);
//            match.setDifficulty(DifficultyState.EASY);
//            match.setStatus(MatchStatus.DRAW);
//
//            matchHistory.addMatch(match);
//
//            return matchHistory;
//        }
//        @Test
//        void startWithEndedMatchTest() {
//
//            MatchHistory matchHistory = createMatchHistoryFor_startWithEndedMatchTest();
//            DifficultyState difficultyState = DifficultyState.EASY;
//            FileWriteRead mockFileWriteRead = mock(FileWriteRead.class);
//
//            Match match = StartGameUtil.returnRunningOrNewMatchOLD(matchHistory, difficultyState, mockFileWriteRead);
//
//            assertEquals(match, matchHistory.getMatches().getLast());
//        }
//
//
//        @Test
//        void startWithEmptyMatchHistoryTest() {
//
//            MatchHistory matchHistory = new MatchHistory();
//            DifficultyState difficultyState = DifficultyState.EASY;
//            FileWriteRead mockFileWriteRead = mock(FileWriteRead.class);
//
//            Match match = StartGameUtil.returnRunningOrNewMatchOLD(matchHistory, difficultyState, mockFileWriteRead);
//
//            assertEquals(match, matchHistory.getMatches().getLast());
//        }
//
//
//}