package jp.adtekfuji.prodcountreporter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static java.util.stream.Collectors.*;


public class StringUtil {


    public static Function<String, String> CreateReplaceFunctor(List<String> patternList, Function<String, String> functor)
    {
        List<Pattern> patterns = patternList
                .stream()
                .map(Pattern::compile)
                .collect(toList());

        Function<String, String> func = functor;
        for(int n=patterns.size()-1; n>=0; --n) {
            Function<String, String> tmp = func;
            Pattern pattern = patterns.get(n);
            func = str->StringUtil.Replace(pattern, str, tmp);
        }
        return func;
    }

    static String Replace(Pattern pattern, String text, Function<String, String> functor)
    {
        if(Objects.isNull(text)) { return null; }

        Matcher matcher = pattern.matcher(text);

        ArrayList<List<Integer>> pos = new ArrayList<>();

        while(matcher.find()) {
            pos.add(Arrays.asList(matcher.start(), matcher.end()));
        }

        if(pos.size()<=0) { return text; }

        int begin = 0;
        StringBuilder sb = new StringBuilder();
        for (List<Integer> po : pos) {
            sb.append(text, begin, po.get(0));
            sb.append(functor.apply(text.substring(po.get(0), po.get(1))));
            begin = po.get(1);
        }
        sb.append(text, begin, text.length());

        return sb.toString();
    }



}
