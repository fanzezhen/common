package com.github.fanzezhen.common.leaned;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

/**
 * @author zezhen.fan
 */
public class Demo {
    public static void main(String[] args) throws ExecutionException, InterruptedException, IOException {
    }

    private static void stringDemo() {
        System.out.println("　1".trim());
        System.out.println("　1".strip());
    }

    private static void collectionDemo() {
        var list = List.of("Java", "Python", "C");
        var copy = List.copyOf(list);
        // true
        System.out.println(list == copy);
        var set = Set.of("Java", "Python");
        var map = Map.of("Java", 1, "Python", 2);
        new HashMap<>(2) {{
            put("Java", 1);
            put("Python", 2);
        }};
    }

    private static void streamDemo() {
        // 1. ofNullable(T t)
        // 以前of(null)报错，现在可以使用ofNullable(null)
        Stream<Object> s = Stream.ofNullable(null);
        s.forEach(System.out::println);

        // 2. takeWhile(Predicate<? super T> predicate)
        // 此方法根据Predicate接口来判断如果为true则取出，生成一个新的流,碰到false即终止判断。
        Stream<Integer> intStream = Stream.of(6, 10, 11, 15, 20);
        Stream<Integer> intTakeStream = intStream.takeWhile(t -> t % 2 == 0);
        System.out.println("intTakeStream：");
        intTakeStream.forEach(System.out::println);

        // 3. dropWhile(Predicate<? super T> predicate)
        // 此方法根据Predicate接口来判断如果为true则丢弃，生成一个新的流,碰到false即终止判断。
        intStream = Stream.of(6, 10, 11, 15, 20);
        Stream<Integer> intDropStream = intStream.dropWhile(t -> t % 2 == 0);
        System.out.println("intDropStream：");
        intDropStream.forEach(System.out::println);

        // 4. iterate重载
        // 以前使用iterate方法生成无限流需要配合limit进行截断
        Stream<Integer> limit = Stream.iterate(1, i -> i + 1).limit(5);
        System.out.println("limit：");
        // 现在重载后这个方法增加了个判断参数
        Stream<Integer> iterate = Stream.iterate(1, i -> i <= 5, i -> i + 1);
        System.out.println("iterate：");
    }

    private static void optionalDemo() {
    }

    /**
     * 发送同步请求
     */
    private static void httpDemo1() throws IOException, InterruptedException {
        var request = HttpRequest.newBuilder()
                .uri(URI.create("https://www.codesheep.cn"))
                .GET()
                .build();
        // 同步请求方式，拿到结果前会阻塞当前线程
        var httpResponse = HttpClient.newHttpClient()
                .send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("拿到结果前会阻塞当前线程...");
        // 打印获取到的网页内容
        System.out.println(httpResponse.body());
    }

    /**
     * 发送异步请求
     */
    private static void httpDemo2() throws InterruptedException, ExecutionException {
        var request = HttpRequest.newBuilder()
                .uri(URI.create("https://www.codesheep.cn"))
                .GET()
                .build();
        CompletableFuture<String> future = HttpClient.newHttpClient().
                sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body);
        System.out.println("先继续干点别的事情...");
        // 打印获取到的网页内容
        System.out.println(future.get());
    }
}
