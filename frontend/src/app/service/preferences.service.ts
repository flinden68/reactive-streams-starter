import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';

import { Observable, of } from 'rxjs';
import { catchError, map, tap } from 'rxjs/operators';

import { Preference } from '../model/preference';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

@Injectable({ providedIn: 'root' })
export class PreferenceService {

  private serviceUrl = 'http://localhost:10000/reactive/streams';  // URL to web api

  constructor(
    private http: HttpClient) { }

  public getPreferences() {
    return this.http.get<Preference[]>(this.serviceUrl + '/preference/all')
      .pipe(
        catchError(this.handleError('getPreferences', []))
      );
  }

  public getPreferenceById(userid:string) {
    return this.http.get<Preference>(this.serviceUrl + '/preference/'+userid)
      .pipe(
        catchError(this.handleError('getPreferenceById', []))
      );
  }

  public savePreferences(preference:Preference): Observable<Preference> {
    return this.http.post(this.serviceUrl + '/preference/save', preference, httpOptions)
      .pipe(
        tap((preference:Preference) => this.log(`added preference w/ id=${preference.id}`)),
        catchError(this.handleError<Preference>('savePreferences'))
      );
  }

  public deletePreference(userid:string) {
    return this.http.delete<String>(this.serviceUrl + '/preference/delete/'+userid)
      .pipe(
        catchError(this.handleError('deletePreference', []))
      );
  }

  public deleteAlllPreferences() {
    return this.http.delete<String>(this.serviceUrl + '/preference/delete/all')
      .pipe(
        catchError(this.handleError('deleteAllPreferences', []))
      );
  }

  /**
   * Handle Http operation that failed.
   * Let the app continue.
   * @param operation - name of the operation that failed
   * @param result - optional value to return as the observable result
   */
  private handleError<T> (operation = 'operation', result?: T) {
    return (error: any): Observable<T> => {

      // TODO: send the error to remote logging infrastructure
      console.error(error); // log to console instead

      // TODO: better job of transforming error for user consumption
      this.log(`${operation} failed: ${error.message}`);

      // Let the app keep running by returning an empty result.
      return of(result as T);
    };
  }

  /** Log a HeroService message with the MessageService */
  private log(message: string) {
    console.log(`PreferenceService: ${message}`);
  }

}
