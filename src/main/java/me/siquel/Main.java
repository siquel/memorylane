package me.siquel;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.StreamSupport;

public class Main {

    public static <T> CompletableFuture<List<T>> allOf(Iterable<CompletableFuture<T>> cfs) {
        var future = CompletableFuture.allOf(
                StreamSupport.stream(cfs.spliterator(), false).toArray(CompletableFuture[]::new));

        return future.thenApply(v -> StreamSupport.stream(cfs.spliterator(), false)
                .map(CompletableFuture::join)
                .toList());
    }

    public static CompletableFuture<HttpResponse<String>> httpGet(final URI uri) {
        HttpRequest request = HttpRequest.newBuilder().GET().uri(uri).build();
        return HttpClient.newBuilder()
                .build()
                .sendAsync(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
    }

    public static void main(String[] args) throws URISyntaxException {
        List<URI> targets = Arrays.asList(
                new URI("https://postman-echo.com/get?foo1=bar1"), // new-line
                new URI("https://postman-echo.com/get?foo2=bar2"));

        List<CompletableFuture<String>> futures = targets.stream()
                .map(target -> httpGet(target).thenApply(HttpResponse::body))
                .toList();

        System.out.println(Main.allOf(futures).join());
    }
}
