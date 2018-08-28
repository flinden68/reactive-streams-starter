export class Topic {
  id:number;
  name:string;
  created:Date;
  modified:Date;

  /*constructor(id:number, name:string, created:Date, modified:Date){
    this.id = id;
    this.name = name;
    this.created = created;
    this.modified = modified;
  }*/

  setName(name:string){
    this.name = name;
  }
}
