export interface UserInfo {
  name: string,
  avatarUrl: string
}

export interface UserFullInfo {
  userReference: ContactReference,
  userInfo: UserInfo
}

export interface ContactReference {
  id: string
}

export interface ContactInfo {
  contactReference: ContactReference,
  name: string,
  avatar: string
}

export interface ContactView {
  contactReference: ContactReference,
  name: string,
  avatar: string,
  lastMessage: string,
  lastMessageDate: string
}

export interface DialogReference {
  id: string
}

export interface DialogInfo {
  dialogReference: DialogReference,
  users: Array<ContactInfo>,
  lastMessage: string,
  lastMessageDate: string
}

export interface DialogView {
  dialogReference: DialogReference,
  contactReference: ContactReference,
  name: string,
  avatar: string,
  lastMessage: string,
  lastMessageDate: string
}

export interface Message {
  dialogReference: DialogReference,
  contactInfo: ContactInfo,
  value: string,
  date: string
}

export interface MessageView {
  contactReference: ContactReference,
  contactName: string,
  contactAvatar: string,
  message: string,
  date: string
}

export interface SettingView  {
  id: number,
  name: string,
  description: string,
  icon: string
}

export interface StatusChangedEvent {
  contact: ContactInfo,
  online: boolean
}

// export class MapLike<T> {
//     private items: { [key: string]: T };
//
//     constructor() {
//         this.items = {};
//     }
//
//     set(key: string, value: T): void {
//         this.items[key] = value;
//     }
//
//     has(key: string): boolean {
//         return key in this.items;
//     }
//
//     get(key: string): T {
//         return this.items[key];
//     }
//
//     values(): T[] {
//         let res: T[] = [];
//         Object.getOwnPropertyNames(this.items)
//             .map((key: string) => {
//                 res.push(this.items[key]);
//             });
//         return res;
//     }
//
//     keys(): string[] {
//         return Object.keys(this.items);
//     }
//
//     entries(): Array<{key: string; value: T}> {
//         let res= [];
//         for (let key of Object.keys(this.items)) {
//             res.push(Object.freeze({key: key, value: this.items[key]}))
//         }
//         return res;
//     }
// }
