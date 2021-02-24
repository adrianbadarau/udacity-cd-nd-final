import { IRecipe } from 'app/shared/model/recipe.model';
import { IList } from 'app/shared/model/list.model';

export interface IItem {
  id?: string;
  name?: string;
  qty?: number;
  unit?: string;
  inCart?: boolean;
  imageContentType?: string;
  image?: any;
  recipes?: IRecipe[];
  list?: IList;
}

export class Item implements IItem {
  constructor(
    public id?: string,
    public name?: string,
    public qty?: number,
    public unit?: string,
    public inCart?: boolean,
    public imageContentType?: string,
    public image?: any,
    public recipes?: IRecipe[],
    public list?: IList
  ) {
    this.inCart = this.inCart || false;
  }
}
