// Created: 03.07.2020
package de.freese.knn.neuron;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Spliterator;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import de.freese.knn.net.neuron.Neuron;
import de.freese.knn.net.neuron.NeuronImpl;
import de.freese.knn.net.neuron.NeuronList;
import org.junit.jupiter.api.Test;

/**
 * @author Thomas Freese
 */
class TestNeuronList
{
    static Neuron[] createNeurons()
    {
        return new Neuron[]
                {
                        new NeuronImpl(null, 0), new NeuronImpl(null, 1), new NeuronImpl(null, 2), new NeuronImpl(null, 3), new NeuronImpl(null, 4)
                };
    }

    static Stream<Neuron[]> createNeuronsAsStream()
    {
        return Stream.of(1).map(v -> createNeurons());
    }

    @Test
    void testForEach()
    {
        Neuron[] neurons = createNeurons();
        NeuronList neuronList = new NeuronList(neurons);

        AtomicInteger atomicInteger = new AtomicInteger(0);

        neuronList.forEach(neuron ->
                assertEquals(atomicInteger.getAndIncrement(), neuron.getLayerIndex()))
        ;

        assertEquals(5, atomicInteger.get());

        // SubList
        NeuronList subList = neuronList.subList(2, 4);

        assertEquals(2, subList.size());

        atomicInteger.set(2);

        subList.forEach(neuron ->
        {
            System.out.println(neuron.getLayerIndex());
            assertEquals(atomicInteger.getAndIncrement(), neuron.getLayerIndex());
        });

        assertEquals(4, atomicInteger.get());
    }

    // @ParameterizedTest
    // @MethodSource("createNeuronsAsStream")
    @Test
    void testIterator()
    {
        Neuron[] neurons = createNeurons();
        NeuronList neuronList = new NeuronList(neurons);

        int i = 0;

        for (Neuron neuron : neuronList)
        {
            assertEquals(i++, neuron.getLayerIndex());
        }

        assertEquals(neurons.length, i);
    }

    @Test
    void testPartitionByModulo()
    {
        List<String> values = List.of("a", "b", "c", "d", "e", "f", "g", "h", "i");

        int parallelism = 4;

        List<List<String>> partitions = getPartitionsByModulo(values, parallelism);

        assertEquals(3, partitions.get(0).size());
        assertEquals(2, partitions.get(1).size());
        assertEquals(2, partitions.get(2).size());
        assertEquals(2, partitions.get(3).size());

        assertEquals("[a, e, i]", partitions.get(0).toString());
        assertEquals("[b, f]", partitions.get(1).toString());
        assertEquals("[c, g]", partitions.get(2).toString());
        assertEquals("[d, h]", partitions.get(3).toString());
    }

    @Test
    void testPartitionBySize()
    {
        // 9
        List<String> values = List.of("a", "b", "c", "d", "e", "f", "g", "h", "i");
        List<List<String>> partitions = getPartitionsBySize(values, 4);

        assertEquals(3, partitions.get(0).size());
        assertEquals(2, partitions.get(1).size());
        assertEquals(2, partitions.get(2).size());
        assertEquals(2, partitions.get(3).size());

        assertEquals("[a, b, c]", partitions.get(0).toString());
        assertEquals("[d, e]", partitions.get(1).toString());
        assertEquals("[f, g]", partitions.get(2).toString());
        assertEquals("[h, i]", partitions.get(3).toString());

        // 10
        values = List.of("a", "b", "c", "d", "e", "f", "g", "h", "i", "j");
        partitions = getPartitionsBySize(values, 4);

        assertEquals(3, partitions.get(0).size());
        assertEquals(3, partitions.get(1).size());
        assertEquals(2, partitions.get(2).size());
        assertEquals(2, partitions.get(3).size());

        assertEquals("[a, b, c]", partitions.get(0).toString());
        assertEquals("[d, e, f]", partitions.get(1).toString());
        assertEquals("[g, h]", partitions.get(2).toString());
        assertEquals("[i, j]", partitions.get(3).toString());

        // 11
        values = List.of("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k");
        partitions = getPartitionsBySize(values, 4);

        assertEquals(3, partitions.get(0).size());
        assertEquals(3, partitions.get(1).size());
        assertEquals(3, partitions.get(2).size());
        assertEquals(2, partitions.get(3).size());

        assertEquals("[a, b, c]", partitions.get(0).toString());
        assertEquals("[d, e, f]", partitions.get(1).toString());
        assertEquals("[g, h, i]", partitions.get(2).toString());
        assertEquals("[j, k]", partitions.get(3).toString());
    }

    // @ParameterizedTest
    // @MethodSource("createNeuronsAsStream")
    @Test
    void testSizeAndIndex()
    {
        Neuron[] neurons = createNeurons();
        NeuronList neuronList = new NeuronList(neurons);

        assertEquals(neurons.length, neuronList.size());

        for (int i = 0; i < neuronList.size(); i++)
        {
            assertEquals(i, neuronList.get(i).getLayerIndex());
        }
    }

    @Test
    void testSpliterator()
    {
        Neuron[] neurons = createNeurons();
        NeuronList neuronList = new NeuronList(neurons);

        Spliterator<Neuron> spliterator = neuronList.spliterator();
        assertEquals(neurons.length, spliterator.getExactSizeIfKnown());

        AtomicInteger atomicInteger = new AtomicInteger(0);

        spliterator.forEachRemaining(neuron ->
                assertEquals(atomicInteger.getAndIncrement(), neuron.getLayerIndex())
        );

        assertEquals(neurons.length, atomicInteger.get());

        // SubList
        spliterator = neuronList.subList(2, 4).spliterator();
        assertEquals(2, spliterator.getExactSizeIfKnown());

        atomicInteger.set(2);

        spliterator.forEachRemaining(neuron ->
                assertEquals(atomicInteger.getAndIncrement(), neuron.getLayerIndex())
        );

        assertEquals(4, atomicInteger.get());
    }

    @Test
    void testStream()
    {
        Neuron[] neurons = createNeurons();
        NeuronList neuronList = new NeuronList(neurons);

        assertEquals(10, neuronList.stream().mapToInt(Neuron::getLayerIndex).sum());
        assertEquals(10, neuronList.stream().parallel().mapToInt(Neuron::getLayerIndex).sum());
        assertEquals(10, neuronList.parallelStream().mapToInt(Neuron::getLayerIndex).sum());
    }

    @Test
    void testSubList()
    {
        Neuron[] neurons = createNeurons();
        NeuronList neuronList = new NeuronList(neurons);

        NeuronList subList = neuronList.subList(1, 2);
        assertEquals(1, subList.size());
        assertEquals(1, subList.get(0).getLayerIndex());

        subList = neuronList.subList(2, 4);
        assertEquals(2, subList.size());
        assertEquals(2, subList.get(0).getLayerIndex());
        assertEquals(3, subList.get(1).getLayerIndex());
        assertEquals(4, subList.get(2).getLayerIndex());
    }

    protected List<List<String>> getPartitionsBySize(final List<String> values, final int parallelism)
    {
        int partitionCount = Math.min(values.size(), parallelism);
        int partitionLength = values.size() / partitionCount;

        int[] partitionSizes = new int[partitionCount];
        Arrays.fill(partitionSizes, partitionLength);

        int sum = partitionCount * partitionLength;

        // Länge der einzelnen Partitionen ist zu groß.
        // Von hinten Index für Index reduzieren bis es passt.
        int index = partitionCount - 1;

        while (sum > values.size())
        {
            partitionSizes[index]--;

            sum--;
            index--;
        }

        // Länge der einzelnen Partitionen ist zu klein.
        // Von vorne Index für Index erhöhen bis es passt.
        index = 0;

        while (sum < values.size())
        {
            partitionSizes[index]++;

            sum++;
            index++;
        }

        List<List<String>> partitions = new ArrayList<>(partitionCount);
        int fromIndex = 0;

        for (int partitionSize : partitionSizes)
        {
            partitions.add(values.subList(fromIndex, fromIndex + partitionSize));

            fromIndex += partitionSize;
        }

        return partitions;
    }

    private List<List<String>> getPartitionsByModulo(final List<String> values, final int parallelism)
    {
        Map<Integer, List<String>> partitionMap = new HashMap<>();

        for (int i = 0; i < values.size(); i++)
        {
            String value = values.get(i);
            int indexToUse = i % parallelism;

            partitionMap.computeIfAbsent(indexToUse, key -> new ArrayList<>()).add(value);
        }

        return new ArrayList<>(partitionMap.values());
    }
}
