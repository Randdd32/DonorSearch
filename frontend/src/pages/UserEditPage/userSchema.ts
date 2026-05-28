import { z } from 'zod';
import { PASSWORD_REGEX, PASSWORD_REQUIREMENTS_MESSAGE } from '../../config/constants';
import { ROLE_LABELS, type UserRole } from '../../types/auth';

const roleKeys = Object.keys(ROLE_LABELS) as [UserRole, ...UserRole[]];

export const getUserSchema = (isNew: boolean) => z.object({
  username: z.string()
    .min(2, 'Логин должен содержать от 2 символов')
    .max(100, 'Логин должен содержать до 100 символов'),
  
  password: isNew
    ? z.string().regex(
        PASSWORD_REGEX, 
        PASSWORD_REQUIREMENTS_MESSAGE
      )
    : z.string().optional().refine(
        (val) => !val || PASSWORD_REGEX.test(val), 
        PASSWORD_REQUIREMENTS_MESSAGE
      ),
      
  role: z.enum(roleKeys, {
    message: 'Пожалуйста, выберите системную роль'
  })
});

export type UserFormValues = z.infer<ReturnType<typeof getUserSchema>>;