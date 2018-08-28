import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';

import {Observable, Observer, of} from 'rxjs';
import { catchError, tap } from 'rxjs/operators';

import {Topic} from "../model/topic";

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

@Injectable({ providedIn: 'root' })
export class TopicService {

  private serviceUrl = 'http://localhost:10000/reactive/streams';  // URL to web api

  constructor(private http: HttpClient) {

  }

  public getTopics() {
    return this.http.get<Topic[]>(this.serviceUrl + '/topic/all')
      .pipe(
        catchError(this.handleError('getTopics', []))
      );
  }

  public getTopicByName(name:string) {
    return this.http.get<Topic>(this.serviceUrl + '/topic/'+name)
      .pipe(
        catchError(this.handleError('getTopicByName', []))
      );
  }

  public createTopic(topic:Topic): Observable<Topic> {
    return this.http.post(this.serviceUrl + '/topic/create', topic, httpOptions)
      .pipe(
        tap((topic:Topic) => this.log(`added topic w/ id=${topic.id}`)),
        catchError(this.handleError<Topic>('createTopic'))
      );
  }

  public deleteTopic(name:string) {
    return this.http.delete<String>(this.serviceUrl + '/topic/delete/'+name)
      .pipe(
        catchError(this.handleError('deleteTopic', []))
      );
  }

  public deleteAllTopics() {
    return this.http.delete<String>(this.serviceUrl + '/topic/delete/all')
      .pipe(
        catchError(this.handleError('deleteAllTopics', []))
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
    console.log(`TopicService: ${message}`);
  }

}
