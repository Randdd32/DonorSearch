import { z } from 'zod';
import { PASSWORD_REGEX, PASSWORD_REQUIREMENTS_MESSAGE } from '../../config/constants';

export const loginSchema = z.object({
  username: z.string()
    .min(2, 'Логин должен содержать от 2 символов')
    .max(100, 'Логин должен содержать до 100 символов'),
  password: z.string()
    .regex(
      PASSWORD_REGEX, 
      PASSWORD_REQUIREMENTS_MESSAGE
    ),
});

export type LoginFormValues = z.infer<typeof loginSchema>;