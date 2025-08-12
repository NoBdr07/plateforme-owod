import { AccountType } from "../enums/account-type.enum";
import { User } from "./user.interface";

export interface SessionState {
  isLogged: boolean;
  isAdmin: boolean;
  userId: string | null;
  accountType: AccountType;   // NONE | DESIGNER | COMPANY
  user?: User | null;
  designerId?: string | null;
  companyId?: string | null;
}