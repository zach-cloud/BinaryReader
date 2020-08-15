package com.github.zachcloud.reader;

import com.github.zachcloud.interfaces.IBinaryReader;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Assert;

public class BinaryReaderStepDefs {

    private IBinaryReader binaryReader;
    private String readString;
    private int readInteger;
    private long readLong;
    private short readShort;
    private byte readByte;
    private byte[] readBytes;

    private byte[] convertBytes(String origin) {
        origin = origin.replace(" ", "");
        byte[] bytes = new byte[origin.length() / 2];
        for (int i = 0; i < bytes.length; i++) {
            int index = i * 2;
            int j = Integer.parseInt(origin.substring(index, index + 2), 16);
            bytes[i] = (byte) j;
        }
        return bytes;
    }

    private void assertByteArraysEqual(byte[] ar1, byte[] ar2) {
        if(ar1.length != ar2.length) {
            Assert.fail("Arrays not same size");
        }
        for(int i = 0; i < ar1.length; i++) {
            Assert.assertEquals(ar1[i], ar2[i]);
        }
    }

    @Given("Bytes {string}")
    public void bytes(String byteRepresentation) {
        binaryReader = new BinaryReader(convertBytes(byteRepresentation));
    }

    @When("A String with {int} length is read")
    public void a_String_with_length_is_read(int len) {
        readString = binaryReader.readString(len);
    }

    @When("A String is read")
    public void a_String_is_read() {
        readString = binaryReader.readString();
    }

    @Then("String result will be {string}")
    public void string_result_will_be(String result) {
        Assert.assertEquals(result, readString);
    }

    @When("An Integer is read")
    public void an_Integer_is_read() {
        readInteger = binaryReader.readInt();
    }

    @Then("Integer result will be {int}")
    public void integer_result_will_be(int result) {
        Assert.assertEquals(result, readInteger);
    }

    @When("A Short is read")
    public void a_Short_is_read() {
        readShort = (short)binaryReader.readShort();
    }

    @Then("Short result will be {int}")
    public void short_result_will_be(int result) {
        Assert.assertEquals(result, readShort);
    }

    @When("A Long is read")
    public void a_Long_is_read() {
        readLong = binaryReader.readLong();
    }

    @Then("Long result will be {string}")
    public void long_result_will_be(String result) {
        Assert.assertEquals(Long.parseLong(result), readLong);
    }

    @When("A Byte is read")
    public void a_Byte_is_read() {
        readByte = binaryReader.readByte();
    }

    @Then("Byte should be {int}")
    public void byte_should_be(int result) {
        Assert.assertEquals(result, readByte);
    }

    @When("A Byte Array of size {int} is read")
    public void a_Byte_Array_of_size_is_read(int size) {
        readBytes = binaryReader.readBytes(size);
    }

    @Then("Byte Array should be {string}")
    public void byte_Array_should_be(String result) {
        assertByteArraysEqual(convertBytes(result), readBytes);
    }

    @When("String {string} is found")
    public void string_is_found(String flag) {
        binaryReader.goTo(flag);
    }

    @When("We go to position {int}")
    public void we_go_to_position(int flag) {
        binaryReader.position(flag);
    }
}
