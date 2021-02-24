export interface INotification {
  id?: string;
  body?: string;
}

export class Notification implements INotification {
  constructor(public id?: string, public body?: string) {}
}
