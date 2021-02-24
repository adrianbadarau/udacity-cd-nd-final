import { IList } from 'app/shared/model/list.model';
import { IItem } from 'app/shared/model/item.model';

export interface IRecipe {
  id?: string;
  name?: string;
  list?: IList;
  item?: IItem;
}

export class Recipe implements IRecipe {
  constructor(public id?: string, public name?: string, public list?: IList, public item?: IItem) {}
}
