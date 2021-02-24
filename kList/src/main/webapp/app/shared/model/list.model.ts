import { IItem } from 'app/shared/model/item.model';
import { IRecipe } from 'app/shared/model/recipe.model';

export interface IList {
  id?: string;
  name?: string;
  items?: IItem[];
  recipes?: IRecipe[];
}

export class List implements IList {
  constructor(public id?: string, public name?: string, public items?: IItem[], public recipes?: IRecipe[]) {}
}
