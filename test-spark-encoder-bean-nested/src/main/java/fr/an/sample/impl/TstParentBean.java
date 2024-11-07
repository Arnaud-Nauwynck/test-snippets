package fr.an.sample.impl;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.val;

@Data
@EqualsAndHashCode
public class TstParentBean {

    protected long x;
    protected long y;
    protected long z;

    protected long t;

    protected String a;
    protected boolean b;

    protected TstBean nested;

    public static TstParentBean createMock(int i) {
        val res = new TstParentBean();
        res.x = 10*i;
        res.y = 10*i+1;
        res.z = 10*i+2;
        res.t = 10*i+3;
        res.a = "test" + i;
        res.b = (i%2==0);
        res.nested = TstBean.createMock(i);
        return res;
    }

}
