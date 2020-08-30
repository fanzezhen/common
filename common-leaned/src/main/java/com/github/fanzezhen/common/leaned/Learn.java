package com.github.fanzezhen.common.leaned;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Learn {
    public static class Key{
        private String k;

        public Key() {
        }

        public Key(String k) {
            this.k = k;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Key)) return false;

            Key key = (Key) o;
            System.out.println("2");
            return k != null ? k.equals(key.k) : key.k == null;
        }

        @Override
        public int hashCode() {
            System.out.println(k != null ? k.hashCode() : 0);
            return k != null ? k.hashCode() : 0;
        }
    }

    public static void main(String[] args) {
        Key k1 = new Key("1");
        Key k2 = new Key("1");
        Map<Key, Object> map = new HashMap<>(){{
           put(k1, "1");
           put(k2, "2");
        }};
        System.out.println(map);
    }
}
