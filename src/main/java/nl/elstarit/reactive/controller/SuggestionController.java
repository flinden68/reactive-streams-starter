package nl.elstarit.reactive.controller;

import lombok.extern.slf4j.Slf4j;
import nl.elstarit.reactive.model.Suggestion;
import nl.elstarit.reactive.model.Topic;
import nl.elstarit.reactive.repository.SuggestionRepository;
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
@RequestMapping("/reactive/streams/suggestion")
public class SuggestionController {

    private SuggestionRepository suggestionRepository;

    public SuggestionController(SuggestionRepository suggestionRepository){
        this.suggestionRepository = suggestionRepository;
    }

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Suggestion> streamAllTweets() {
        return suggestionRepository.findAll();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/all", produces = "application/json")
    public Flux<Suggestion> getAllSuggestions(){
        return suggestionRepository.findAll();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{name}", produces = "application/json")
    public Mono<Suggestion> getSuggestionyName(@PathVariable(value = "name") String suggestionName){
        return suggestionRepository.findByName(suggestionName);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/create", consumes = "application/json")
    public Mono<Suggestion> createSuggestion(@Valid@RequestBody Suggestion suggestion){
        return suggestionRepository.findByName(suggestion.getName())
                .flatMap(existingSuggestion -> Mono.error(new RuntimeException("Suggestion already exists: "+existingSuggestion.getName())))
                .then(suggestionRepository.save(suggestion));
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/delete/{name}")
    public Mono<ResponseEntity<Void>> deleteSuggestion(@PathVariable(value = "name") String suggestionName){
        return suggestionRepository.findByName(suggestionName)
                .flatMap(existingSuggestion ->
                        suggestionRepository.delete(existingSuggestion)
                                .then(Mono.just(new ResponseEntity<Void>(HttpStatus.OK)))
                )
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/delete/all")
    public Mono<ResponseEntity<Void>> deleteAllSuggestions(){
        return suggestionRepository.deleteAll()
                    .then(Mono.just(new ResponseEntity<Void>(HttpStatus.OK))
                )
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
