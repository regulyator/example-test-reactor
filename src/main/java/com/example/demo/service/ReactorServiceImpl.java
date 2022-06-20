package com.example.demo.service;

import com.example.demo.dto.RequestTarget;
import com.example.demo.dto.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.atomic.AtomicBoolean;

@Component
@Slf4j
public class ReactorServiceImpl implements ReactorService {

    private final ExternalService externalService;

    public ReactorServiceImpl(ExternalService externalService) {
        this.externalService = externalService;
    }

    @Override
    public void process() {
        AtomicBoolean needRepeat = new AtomicBoolean(true);
        Mono<Response> monoRequest = Mono.fromCallable(() -> externalService.getSomeInfo());

        Mono.defer(() -> monoRequest)
                .doOnNext(response -> needRepeat.set(response.isRepeat()))
                .doOnNext(response -> log.info("Response info size {}", response.getSomeInformation().size()))
                .flatMapMany(response -> Flux.fromIterable(response.getSomeInformation()))
                .repeat(needRepeat::get)
                .map(info -> RequestTarget.builder()
                        .info(info)
                        .build())
                .parallel(10, 1)
                .runOn(Schedulers.elastic())
                .doOnNext(requestTarget -> log.info("Request {}", requestTarget.getInfo()))
                .flatMap(requestTarget ->
                        Mono.fromCallable(() -> externalService.placeSomeInfo(requestTarget))
                                .subscribeOn(Schedulers.elastic()), false, 10, 1)
                .doOnNext(responseTarget -> log.info("================RESPONSE================"))
                .doOnError(throwable -> log.info("Error {}", throwable.getMessage()))
                .doOnComplete(() -> log.info("Finish work"))
                //тут мы "сворачиваем" параллельный флакс и возвращаемся в бренный, непараллельный мир
                // и дожидаемся пока не получим последний элемент
                .sequential()
                .blockLast();

    }
}
