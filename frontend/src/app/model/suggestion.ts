export class Suggestion {
  id:number;
  name:string;
  created:Date;
  modified:Date;

  setName(name:string){
    this.name = name;
  }
}
