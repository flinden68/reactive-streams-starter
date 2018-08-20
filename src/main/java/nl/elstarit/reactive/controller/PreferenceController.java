package nl.rabobank.gict.crmvirtual.relevance.sectorknowledge.topicmanagementservice.controller;

import nl.rabobank.gict.crmvirtual.relevance.sectorknowledge.topicmanagementservice.model.Preference;
import nl.rabobank.gict.crmvirtual.relevance.sectorknowledge.topicmanagementservice.repository.PreferenceRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RestController
@RequestMapping("/sector-knowledge/topic-management-service/preference")
public class PreferenceController {

    private PreferenceRepository preferenceRepository;

    public PreferenceController(PreferenceRepository preferenceRepository){
        this.preferenceRepository = preferenceRepository;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{userid}", produces = "application/json")
    public Mono<ResponseEntity<Preference>> getPreferenceById(@PathVariable(value = "userid") String userid){
        return preferenceRepository.findByUserid(userid)
                .map(savedPreference -> ResponseEntity.ok(savedPreference))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @RequestMapping(method = RequestMethod.GET, value = "/all", produces = "application/json")
    public Flux<Preference> getAllPreferences(){
        return preferenceRepository.findAll();
    }

    @RequestMapping(method = RequestMethod.POST, value = "/save", consumes = "application/json")
    public Mono<Preference> savePreference(@Valid @RequestBody Preference preference){
        return preferenceRepository.save(preference);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/delete/{userId}")
    public Mono<ResponseEntity<String>> deletePreference(@PathVariable("userId") final String userId){
        return preferenceRepository.findByUserid(userId)
                .flatMap(savedPreference -> preferenceRepository.delete(savedPreference))
                .then(Mono.just(new ResponseEntity<String>(userId, HttpStatus.OK)))
                .defaultIfEmpty(new ResponseEntity<String>(userId, HttpStatus.NOT_FOUND));
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/delete/all")
    public Mono<ResponseEntity<Void>> deleteAllPreferences(){
        return preferenceRepository.deleteAll()
                .then(Mono.just(new ResponseEntity<Void>(HttpStatus.OK))
                )
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
