package login;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import tictactoe.api.errors.PasswordStrengthError;
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

                Arguments.of(""),
                Arguments.of("1dQ!"),
                Arguments.of("hallo123456"),
                Arguments.of("hallo.....()("),
                Arguments.of("hallo123..-="),
                Arguments.of("HALLO11223..-?"),
                Arguments.of("123.....jjjjjjjjj"),
                Arguments.of("aaa"),
                Arguments.of("AAA"),
                Arguments.of("..."),
                Arguments.of("111"),
                Arguments.of("aaaaaaaaaaaaaaaaaasasasasasasasasasasasasasasasasasasa")
        );
    }






    @ParameterizedTest
    @MethodSource("generatePasswordsForFalseTest")
    void passwordsFalseTest(String password) {

        Exception exception = assertThrows(Exception.class, () ->
                PasswordUtil.isPasswordValid(password));

        assertInstanceOf(PasswordStrengthError.class, exception);
    }

    @ParameterizedTest
    @MethodSource("generatePasswordsForTrueTest")
    void passwordsTrueTest(String password, boolean expected) {

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
