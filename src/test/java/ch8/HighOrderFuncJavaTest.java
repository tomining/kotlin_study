package ch8;

import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function1;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static ch8.HighOrderFuncKt.processTheAnswer;

public class HighOrderFuncJavaTest {
    @Test
    public void 자바에서_Kotlin_함수타입_호출하기() {
        processTheAnswer(number -> number + 1);

        processTheAnswer(
                new Function1<Integer, Integer>() {
                    @Override
                    public Integer invoke(Integer number) {
                        System.out.println(number);
                        return number + 1;
                    }
                }
        );
    }

    @Test
    public void 반환타입이_Unit인_경우_명시적으로_Unit을_반환해_주어야_한다() {
        List<String> strings = new ArrayList<>();
        strings.add("42");

        CollectionsKt.forEach(strings, s -> {
            System.out.println(s);
            return Unit.INSTANCE;
        });
    }

    static String readFirstLineFromFile(String path) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            return br.readLine();
        }
    }
}
