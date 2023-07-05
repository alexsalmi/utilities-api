package com.asalmi.utilities_api.utils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class UtilsTest {

  @InjectMocks
  private Utils utils;

  @Test
  public void hashString_Success() throws Exception {
    utils.hashString("string");
  }
}
