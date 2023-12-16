package fr.an.tests;

import fr.an.tests.classgraphduplicate.CommonPrefix;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class CommonPrefixTest {

    @Test
    public void testFindCommonPrefix() {
        Assert.assertEquals("aaa", CommonPrefix.findCommonPrefix(List.of("aaab", "aaabc", "aaacd")));
        Assert.assertEquals("META-INF/", CommonPrefix.findCommonPrefix(List.of("META-INF/NOTICE", "META-INF/LICENSE")));
    }
}
