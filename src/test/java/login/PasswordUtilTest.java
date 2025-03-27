package login;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import tictactoe.api.errors.ErrorCodes;
import tictactoe.api.errors.SignUpError;
import tictactoe.login.HashService;
import tictactoe.login.PasswordUtil;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;


public class PasswordUtilTest {



    private static Stream<Arguments> generatePasswordsForTrueTest() {
        return Stream.of(
                Arguments.of("1!eeeQWrtt", true),
                Arguments.of("1234567!!Qü", true),
                Arguments.of("1234567!!Qä", true),
                Arguments.of("1234567!!Qö", true),
                Arguments.of("1234567!!Qß", true),
                Arguments.of("1234567!!aÜ", true),
                Arguments.of("1234567!!aÄ", true),
                Arguments.of("1234567!!aÖ", true)
        );
    }

    private static Stream<Arguments> generatePasswordsForFalseTest() {
        return Stream.of(

                Arguments.of("", 8), //empty
                Arguments.of("1dQ!", 9),  //too short
                Arguments.of("hallo123456", 11),  //no uppercase
                Arguments.of("hallo..122", 11),  //no uppercase
                Arguments.of("hallo123..-=", 11),  //no uppercase
                Arguments.of("HALLO11223..-?", 10),  //no lowercase
                Arguments.of("PASSWORD1122!!.", 10),  //no lowercase
                Arguments.of("PASSword!!.", 12),  //no digit
                Arguments.of("NoNumber!!.", 12),  //no digit
                Arguments.of("NoSpecial1212", 13),  //no special character
                Arguments.of("Password1212", 13),  //no special character
                Arguments.of("aaa", 9), //too short
                Arguments.of("AAA", 9), //too short
                Arguments.of("...", 9), //too short
                Arguments.of("111", 9), //too short
                Arguments.of("aaaaaaaaaaaaaaaaaasasasasasasasasasasasasasasasasasasa", 11) //no uppercase
        );
    }






    @ParameterizedTest
    @MethodSource("generatePasswordsForFalseTest")
    void passwordsFalseTest(String password, int code) {

        Exception exception = assertThrows(Exception.class, () ->
                PasswordUtil.isPasswordValid(password));

        assertEquals(ErrorCodes.decodeCode(exception.getMessage()), code);
        //assertInstanceOf(PasswordStrengthError.class, exception);
    }

    @ParameterizedTest
    @MethodSource("generatePasswordsForTrueTest")
    void passwordsTrueTest(String password, boolean expected) throws SignUpError {

        boolean bool = PasswordUtil.isPasswordValid(password);

        assertEquals(expected, bool);
    }

    @Test
    void checkPasswordTestTrue(){
        String hashedPassword = HashService.hash("myNewPassword12_:");

        boolean bool = PasswordUtil.checkPassword("myNewPassword12_:", hashedPassword);

        assertTrue(bool);
    }

    @Test
    void checkPasswordTestFalse(){
        String hashedPassword = HashService.hash("not my password");

        boolean bool = PasswordUtil.checkPassword("myNewPassword12_:", hashedPassword);

        assertFalse(bool);
    }




}
