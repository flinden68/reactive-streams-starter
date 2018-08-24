import {Component, OnInit} from '@angular/core';
import {PreferenceService} from "./service/preferences.service";
import {Preference} from "./model/preference";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  title = 'Reactive Streams Demo';

  preferences : Preference[];

  constructor(private preferenceService: PreferenceService) { }

  ngOnInit() {
    this.getPreferences();
  }

  getPreferences(){
    this.preferenceService.getPreferences()
      .subscribe(preferences => this.preferences = preferences);
  }

  deletePreference(preference: Preference):void {
    this.preferences = this.preferences.filter(h => h !== preference);
    this.preferenceService.deletePreference(preference.userid).subscribe();
  }
}
