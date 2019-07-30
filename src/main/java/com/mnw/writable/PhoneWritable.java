package com.mnw.writable;

import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * Created by shaodi.chen on 2019/7/30.
 */
@Data
@ToString
public class PhoneWritable {
    private List<ContactWritable> phoneMapList;
}
