import {Component, Input, OnInit} from '@angular/core';
import {PreferenceService} from "./service/preferences.service";
import {Preference} from "./model/preference";
import {Topic} from "./model/topic";
import {TopicService} from "./service/topics.service";
import {Suggestion} from "./model/suggestion";
import {SuggestionService} from "./service/suggestions.service";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  title = 'Reactive Streams Demo';

  preferences : Preference[];
  topics : Topic[];
  suggestions: Suggestion[];

  topic : Topic;
  suggestion: Suggestion;
  //topicName : string = '';

  constructor(private preferenceService: PreferenceService, private topicService : TopicService, private suggestionService : SuggestionService) { }

  ngOnInit() {
    //this.getPreferences();
    this.loadTopics();
  }

  loadPreferences(){
    this.preferenceService.getPreferences()
      .subscribe(preferences => this.preferences = preferences);
  }

  deletePreference(preference: Preference):void {
    this.preferences = this.preferences.filter(h => h !== preference);
    this.preferenceService.deletePreference(preference.userid).subscribe();
  }

  loadTopics(){
    this.topicService.getTopics()
      .subscribe(topics => this.topics = topics);
  }

  createTopic(topicName:string){
    if(topicName){
      let newTopic = new Topic();
      newTopic.setName(topicName)
      this.topicService.createTopic(newTopic)
        .subscribe(topic => this.topics.push(topic));
    }
  }

  deleteTopic(topic: Topic):void {
    this.topics = this.topics.filter(h => h !== topic);
    this.topicService.deleteTopic(topic.name).subscribe();
  }

  loadSuggestions(){
    this.suggestionService.getSuggestions()
      .subscribe(suggestions => this.suggestions = suggestions);
  }

  createSuggestion(suggestionName:string){
    if(suggestionName){
      let newSuggestion = new Suggestion();
      newSuggestion.setName(suggestionName)
      this.suggestionService.createSuggestion(newSuggestion)
        .subscribe(suggestion => this.suggestions.push(suggestion));
    }
  }

  deleteSuggestion(suggestion: Suggestion):void {
    this.suggestions = this.suggestions.filter(h => h !== suggestion);
    this.suggestionService.deleteSuggestion(suggestion.name).subscribe();
  }
}
