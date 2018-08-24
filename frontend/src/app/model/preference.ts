import {Topic} from "./topic";

export class Preference {
  id:number;
  userid:string;
  created:Date;
  modified:Date;
  topics: Topic[];

  constructor(id:number, userid:string, created:Date, modified:Date, topics: Topic[]){
    this.id = id;
    this.userid = userid;
    this.created = created;
    this.modified = modified;
    this.topics = topics;
  }
}
