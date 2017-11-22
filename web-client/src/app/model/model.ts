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
