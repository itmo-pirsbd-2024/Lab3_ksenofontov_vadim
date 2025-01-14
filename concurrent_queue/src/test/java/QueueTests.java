import org.example.LinkedListQueue;
import org.example.Main;
import org.junit.jupiter.api.Test;

import java.util.Random;
import java.util.stream.IntStream;


class QueueTests {

    int[] generateArray(int numberOfElements) {
        Random random = new Random();
        random.setSeed(0);
        return IntStream.generate(() -> random.nextInt(numberOfElements * 5)).limit(numberOfElements).toArray();
    }

    @Test
    void BuiltinSortTest() {

        var numberOfElements = 5_000_000;

        int[] array = generateArray(numberOfElements);

        LinkedListQueue<Integer> queue = new LinkedListQueue<Integer>();

        for (int i = 0; i < numberOfElements; i++) {
            queue.add(array[i]);
        }

        for (int i = 0; i < numberOfElements; i++) {
            var currentElement = queue.poll();

            assert(currentElement == array[i]);
        }

        assert(queue.isEmpty());
    }
}