import {Component, Input, OnInit} from '@angular/core';
import {PreferenceService} from "./service/preferences.service";
import {Preference} from "./model/preference";
import {Topic} from "./model/topic";
import {TopicService} from "./service/topics.service";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  title = 'Reactive Streams Demo';

  preferences : Preference[];
  topics : Topic[];

  @Input() topic : Topic

  constructor(private preferenceService: PreferenceService, private topicService : TopicService) { }

  ngOnInit() {
    //this.getPreferences();
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

  createTopic(){
    this.topicService.createTopic(this.topic)
      .subscribe(topic => this.topics.push(topic));
  }

  deleteTopic(topic: Topic):void {
    this.topics = this.topics.filter(h => h !== topic);
    this.topicService.deleteTopic(topic.name).subscribe();
  }
}
