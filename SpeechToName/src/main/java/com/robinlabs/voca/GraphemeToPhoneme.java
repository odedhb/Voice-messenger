package com.robinlabs.voca;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by oded on 3/31/14.
 */
public class GraphemeToPhoneme {
//        http://www.dyslexia-reading-well.com/44-phonemes-in-english.html


    static TreeMap<String, String> graphemeToPhonemeMap() {
        TreeMap<String, String> graphemeToPhonemeSortedMap = new TreeMap<String, String>();

        HashMap<String, String> arrayMap = new HashMap<String, String>();

        //consonants
        arrayMap.put("/b/", "b, bb");
        arrayMap.put("/d/", "d, dd, ed");
        arrayMap.put("/f/", "f, ff, ph, gh, lf, ft");
        arrayMap.put("/g/", "g, gg, gh,gu,gue");
        arrayMap.put("/h/", "h, wh");
        arrayMap.put("/J/", "j, ge, g, dge, di, gg");
        arrayMap.put("/k/", "k, c, ch, cc, lk, qu ,q(u), ck, x");
        arrayMap.put("/l/", "l, ll");
        arrayMap.put("/m/", "m, mm, mb, mn, lm");
        arrayMap.put("/n/", "n, nn,kn, gn, pn");
        arrayMap.put("/p/", "p, pp");
        arrayMap.put("/r/", "r, rr, wr, rh");
        arrayMap.put("/s/", "s, ss, c, sc, ps, st, ce, se");
        arrayMap.put("/t/", "t, tt, th, ed");
        arrayMap.put("/v/", "v, f, ph, ve");
        arrayMap.put("/w/", "w, wh, u, o");
        arrayMap.put("/y/", "y, i, j");
        arrayMap.put("/z/", "z, zz, s, ss, x, ze, se");


        //vowels
        arrayMap.put("/a/", "a, ai, au");
        arrayMap.put("/ā/", "a, ai, eigh, aigh, ay, er, et, ei, au, a_e, ea, ey");
        arrayMap.put("/e/", "e, ea, u, ie, ai, a, eo, ei, ae, ay");
        arrayMap.put("/ē/", "e, ee, ea, y, ey, oe, ie, i, ei, eo, ay");
        arrayMap.put("/i/", "i, e, o, u, ui, y, ie");
        arrayMap.put("/ī/", "i, y, igh, ie, uy, ye, ai, is, eigh, i_e");
        arrayMap.put("/o/", "o, a, ho, au, aw, ough");
        arrayMap.put("/ō/", "o, oa, o_e, oe, ow, ough, eau, oo, ew");
        arrayMap.put("/oo/", "o, oo, u,ou");
        arrayMap.put("/u/", "u, o, oo, ou");
        arrayMap.put("/ū/", "o, oo, ew, ue, u_e, oe, ough, ui, oew, ou");
        arrayMap.put("/y//ü/", "u, you, ew, iew, yu, ul, eue, eau, ieu, eu");
        arrayMap.put("/oi/", "oi, oy, uoy");
        arrayMap.put("/ow/", "ow, ou, ough");
        arrayMap.put("/ə/", "a, er, i, ar, our, or, e, ur, re, eur");

        //R Controlled Vowels
        arrayMap.put("/ã/", "air, are, ear, ere, eir, ayer");
        arrayMap.put("/ä/", "a, ar, au, er, ear");
        arrayMap.put("/û/", "ir, er, ur, ear, or, our, yr");
        arrayMap.put("/ô/", "aw, a, or, oor, ore, oar, our, augh, ar, ough, au");
        arrayMap.put("/ēə/", "ear, eer, ere, ier");
        arrayMap.put("/üə/", "ure, our");

        //Digraphs
        arrayMap.put("/zh/", "s, si, z");
        arrayMap.put("/ch/", "ch, tch, tu, ti, te");
        arrayMap.put("/sh/", "sh, ce, s, ci, si, ch, sci, ti");
        arrayMap.put("/th/", "th");
        arrayMap.put("/ng/", "ng, n, ngue");


        for (Map.Entry<String, String> entry : arrayMap.entrySet()) {
            String[] split = entry.getValue().split(",");
            List<String> graphemes = Arrays.asList(split);
            for (String phoneme : graphemes) {
                graphemeToPhonemeSortedMap.put(phoneme.trim(), entry.getKey());
            }

        }
        return graphemeToPhonemeSortedMap;
    }
}
