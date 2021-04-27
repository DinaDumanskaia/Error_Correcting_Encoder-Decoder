package correcter;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Main {
    private static final Random RANDOM = new Random(System.currentTimeMillis());

    public static void main(String[] args) throws IOException {
        String mode = getUserInput();

        switch (mode) {
            case ("encode"):
                encodeFile();
                break;
            case ("send"):
                sendFile();
                break;
            case ("decode"):
                decodeFile();
                break;
            default:
                System.out.println("Error. Wrong input");
                break;
        }

        /*byte[] inputLine = getBinaryFormTextFromFile(linesAmount);
        byte[] lineWithErrors = makeBitErrors(inputLine);
        writeBytesInFile(lineWithErrors);*/

    }

    private static void decodeFile() throws IOException {
        String fileNameToReadFrom = "received.txt";
        String fileToWriteIn = "decoded.txt";

        //byte[] listOfBytes = readLineFromInput();
        byte[] listOfBytes = getBinaryFormTextFromFile(fileNameToReadFrom);
        List<String> listOfCorrectBytesInIntegerList = decodingHammingCode(listOfBytes); //makeBinaryStringCorrect(listOfBytes);

        writeBytesToFile(convertToBinaryFromStringList(listOfCorrectBytesInIntegerList), fileToWriteIn);
    }

    private static List<String> decodingHammingCode(byte[] listOfBytes) {
        List<Integer> listOfInt = getAllIntegersFromByteArray(listOfBytes);
        List<Boolean> list = new ArrayList<>();

        for (int i = 0; i < listOfInt.size(); ) {
            int one = listOfInt.get(i);
            int two = listOfInt.get(i + 1);
            int three = listOfInt.get(i + 2);
            int four = listOfInt.get(i + 3);
            int five = listOfInt.get(i + 4);
            int six = listOfInt.get(i + 5);
            int seven = listOfInt.get(i + 6);
            int eight = listOfInt.get(i + 7);

            boolean oneIsTrue = isEqual(one, three, five, seven);
            boolean twoIsTrue = isEqual(two, three, six, seven);
            boolean fourIsTrue = isEqual(four, five, six, seven);

            if (eight != 0 || (!oneIsTrue && twoIsTrue && fourIsTrue) || (!twoIsTrue && oneIsTrue && fourIsTrue) || (!fourIsTrue && oneIsTrue && twoIsTrue)) {
                list.add(isNullOrOne(three));
                list.add(isNullOrOne(five));
                list.add(isNullOrOne(six));
                list.add(isNullOrOne(seven));
            } else {
                if (!oneIsTrue && !twoIsTrue && fourIsTrue) {
                    three = (three + 1) % 2;
                } else if (!oneIsTrue && twoIsTrue && !fourIsTrue) {
                    five = (five + 1) % 2;
                } else if (!oneIsTrue && !twoIsTrue && !fourIsTrue){
                    seven = (seven + 1) % 2;
                } else {
                    six = (six + 1) % 2;
                }
                list.add(isNullOrOne(three));
                list.add(isNullOrOne(five));
                list.add(isNullOrOne(six));
                list.add(isNullOrOne(seven));
            }
            i += 8;
        }
        return makeStringList(list);
    }

    private static boolean isEqual(int one, int two, int three, int four) {
        return one == (two + three + four) % 2;
    }

    private static List<String> makeBinaryStringCorrect(byte[] listOfBytes) {
        List<Integer> listOfInt = getAllIntegersFromByteArray(listOfBytes);
        List<Boolean> list = new ArrayList<>();
        for (int i = 0; i < listOfInt.size(); ) {
            int one = listOfInt.get(i);
            int two = listOfInt.get(i + 1);
            int three = listOfInt.get(i + 2);
            int four = listOfInt.get(i + 3);
            int five = listOfInt.get(i + 4);
            int six = listOfInt.get(i + 5);
            int seven = listOfInt.get(i + 6);
            int eight = listOfInt.get(i + 7);

            if (seven != eight) {
                list.add(isNullOrOne(one));
                list.add(isNullOrOne(three));
                list.add(isNullOrOne(five));
            } else {
                if (one != two) {
                    if ((one + three + five) % 2 != seven) {
                        one = two;
                    }
                } else if (three != four) {
                    if ((one + three + five) % 2 != seven) {
                        three = four;
                    }
                } else if (five != six) {
                    if ((one + three + five) % 2 != seven) {
                        five = six;
                    }
                }
                list.add(isNullOrOne(one));
                list.add(isNullOrOne(three));
                list.add(isNullOrOne(five));
            }
            i += 8;
        }
        return makeStringList(list);
    }

    private static List<String> makeStringList(List<Boolean> list) {
        List<String> listOfStrings = new ArrayList<>();
        for (int i = 0; i < list.size(); ) {
            if (list.size() - i >= 8) {
                StringBuilder builder = new StringBuilder();
                for (int u = i; u < i + 8; u++) {
                    if (list.get(u)) {
                        builder.append('0');
                    } else {
                        builder.append('1');
                    }
                }
                listOfStrings.add(builder.toString());
                i += 8;
            } else {
                i = list.size();
            }
        }
        return listOfStrings;
    }

    private static Boolean isNullOrOne(int one) {
        return one == 0;
    }

    private static void sendFile() throws IOException {
        String fileNameToReadFrom = "encoded.txt";
        String fileToWriteIn = "received.txt";

        byte[] listOfInputBytes = getBinaryFormTextFromFile(fileNameToReadFrom);
        byte[] listBitErrors = makeBitErrors(listOfInputBytes);

        writeBytesToFile(listBitErrors, fileToWriteIn);
    }

    private static void encodeFile() throws IOException {
        String fileNameToReadFrom = "send.txt";
        String fileToWriteIn = "encoded.txt";

        //byte[] listOfBytes = readLineFromInput();
        byte[] listOfBytes = getBinaryFormTextFromFile(fileNameToReadFrom);
        List<Integer> allSymbolsFromList = getAllIntegersFromByteArray(listOfBytes);

        List<String> newBinaryNumbers = makingNewLinesUsingHammingCode(allSymbolsFromList); //makeParityLines(allSymbolsFromList);

         writeBytesToFile(convertToBinaryFromStringList(newBinaryNumbers), fileToWriteIn);
    }

    private static List<String> makingNewLinesUsingHammingCode(List<Integer> allSymbolsFromList) {
        List<String> listOfStrings = new ArrayList<>();
        for (int i = 0; i < allSymbolsFromList.size(); ) {
            StringBuilder builder = new StringBuilder();
            int three = allSymbolsFromList.get(i);
            int five = allSymbolsFromList.get(i + 1);
            int six = allSymbolsFromList.get(i + 2);
            int seven = allSymbolsFromList.get(i + 3);
            int eight = 0;

            int one = (three + five + seven) % 2;
            int two = (three + six + seven) % 2;
            int four = (five + six + seven) % 2;

            builder.append(one);
            builder.append(two);
            builder.append(three);
            builder.append(four);
            builder.append(five);
            builder.append(six);
            builder.append(seven);
            builder.append(eight);

            listOfStrings.add(builder.toString());
            i += 4;
        }
        return listOfStrings;
    }

    private static byte[] convertToBinaryFromStringList(List<String> listOfBytes) {
        byte[] retValue = new byte[listOfBytes.size()];
        for(int i = 0; i < listOfBytes.size(); i++) {
            String byteString = listOfBytes.get(i);
            byte b = (byte)Integer.parseInt(byteString, 2);
            retValue[i] = b;
        }
        return retValue;
    }

    private static List<String> makeParityLines(List<Integer> allSymbolsFromList) {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < allSymbolsFromList.size(); ) {
            StringBuilder builder = new StringBuilder();
            int sum = 0;
            if (allSymbolsFromList.size() - i >= 3) {
                for (int u = i; u < i + 3; u++) {
                    builder.append(allSymbolsFromList.get(u));
                    builder.append(allSymbolsFromList.get(u));
                    sum += allSymbolsFromList.get(u);
                }
                builder.append(sum % 2);
                builder.append(sum % 2);
                i += 3;

            } else {
                for (int u = i; u < allSymbolsFromList.size(); u++) {
                    builder.append(allSymbolsFromList.get(u));
                    builder.append(allSymbolsFromList.get(u));
                    sum += allSymbolsFromList.get(u);
                }
                if (allSymbolsFromList.size() - i == 2) {
                    builder.append(0);
                    builder.append(0);
                } else {
                    builder.append(0);
                    builder.append(0);
                    builder.append(0);
                    builder.append(0);
                }
                builder.append(sum % 2);
                builder.append(sum % 2);

                i = allSymbolsFromList.size();
            }
            list.add(builder.toString());
        }
        return list;
    }

    private static List<Integer> getAllIntegersFromByteArray(byte[] listOfBytes) {
        List<Integer> list = new ArrayList<>();
        for(int i = 0; i < listOfBytes.length; i++) {
            int oneByte = (int)listOfBytes[i];
            for (int u = 7; u >= 0; u--) {
                int binaryDigit = getBinaryDigit(oneByte, u);
                list.add(binaryDigit);
            }
        }
        return list;
    }

    private static Integer getBinaryDigit(int number, int position) {
        int mask = (int)Math.pow(2, position);
        int masked = number & mask;
        return masked >> position;
    }

    private static String getUserInput() {
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine();
    }

    private static void writeBytesInFile(byte[] lineWithErrors) throws IOException {
        String fileToWriteIn = "received.txt";
        Files.write(Paths.get(fileToWriteIn), lineWithErrors);
    }

    private static void writeBytesToFile(byte[] bytes, String fileName) throws IOException {
        Path path = Paths.get(fileName);

        try (FileOutputStream stream = new FileOutputStream(fileName)) {
            stream.write(bytes);
        }
    }

    private static byte[] makeBitErrors(byte[] inputLine) {
        byte[] newLine = new byte[inputLine.length];
        for (int i = 0; i < inputLine.length; i++) {
            int position = RANDOM.nextInt(8);
            newLine[i] = inputLine[i] ^= (1 << position);
        }
        return newLine;
    }

    private static byte[] getBinaryFormTextFromFile(String fileName) throws IOException {
        Path path = Paths.get(fileName);
        //считываем содержимое файла в массив байт

        byte[] array =  Files.readAllBytes(path);
        /*for (int i = 0; i < array.length; i++) {
            System.out.println(array[i]);
        }*/

        /*List<String> list = Files.readAllLines(path);
        for (int i = 0; i < list.size(); i++) {
            System.out.println(list.get(i));
        }*/

        return array;
    }
}
