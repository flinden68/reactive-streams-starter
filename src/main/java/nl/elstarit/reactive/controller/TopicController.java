package nl.elstarit.reactive.controller;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import nl.elstarit.reactive.model.Topic;
import nl.elstarit.reactive.repository.TopicRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/reactive/streams/topic")
@Api(tags = "Topic Controller")
public class TopicController {

    private TopicRepository topicRepository;

    public TopicController(TopicRepository topicRepository){
        this.topicRepository = topicRepository;
    }

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Topic> streamAllTweets() {
        return topicRepository.findAll();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/all", produces = "application/json")
    public Flux<Topic> getAllTopics(){
        return topicRepository.findAll();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{name}", produces = "application/json")
    public Mono<Topic> getTopicByName(@PathVariable(value = "name") String topicName){
        return topicRepository.findByName(topicName);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/create", consumes = "application/json")
    public Mono<Topic> createTopic(@Valid@RequestBody Topic topic){
        return topicRepository.findByName(topic.getName())
                .flatMap(existingTopic -> Mono.error(new RuntimeException("Topic already exists: "+existingTopic.getName())))
                .then(topicRepository.save(topic));
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/delete/{name}")
    public Mono<ResponseEntity<Void>> deleteTopic(@PathVariable(value = "name") String topicName){
        return topicRepository.findByName(topicName)
                .flatMap(existingTopic ->
                        topicRepository.delete(existingTopic)
                                .then(Mono.just(new ResponseEntity<Void>(HttpStatus.OK)))
                )
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/delete/all")
    public Mono<ResponseEntity<Void>> deleteAllTopics(){
        return topicRepository.deleteAll()
                    .then(Mono.just(new ResponseEntity<Void>(HttpStatus.OK))
                )
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
