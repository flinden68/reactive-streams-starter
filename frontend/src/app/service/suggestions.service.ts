import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';

import {Observable, Observer, of} from 'rxjs';
import { catchError, tap } from 'rxjs/operators';

import {Suggestion} from "../model/suggestion";

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

@Injectable({ providedIn: 'root' })
export class SuggestionService {

  private serviceUrl = 'http://localhost:10000/reactive/streams';  // URL to web api

  constructor(private http: HttpClient) {

  }

  public getSuggestions() {
    return this.http.get<Suggestion[]>(this.serviceUrl + '/suggestion/all')
      .pipe(
        catchError(this.handleError('getSuggestions', []))
      );
  }

  public getSuggestionByName(name:string) {
    return this.http.get<Suggestion>(this.serviceUrl + '/suggestion/'+name)
      .pipe(
        catchError(this.handleError('getSuggestionByName', []))
      );
  }

  public createSuggestion(suggestion:Suggestion): Observable<Suggestion> {
    return this.http.post(this.serviceUrl + '/suggestion/create', suggestion, httpOptions)
      .pipe(
        tap((suggestion:Suggestion) => this.log(`added suggestion w/ id=${suggestion.id}`)),
        catchError(this.handleError<Suggestion>('createSuggestion'))
      );
  }

  public deleteSuggestion(name:string) {
    return this.http.delete<String>(this.serviceUrl + '/suggestion/delete/'+name)
      .pipe(
        catchError(this.handleError('deleteSuggestion', []))
      );
  }

  public deleteAllSuggestions() {
    return this.http.delete<String>(this.serviceUrl + '/suggestion/delete/all')
      .pipe(
        catchError(this.handleError('deleteAllSuggestions', []))
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

  private log(message: string) {
    console.log(`SuggestionService: ${message}`);
  }

}
