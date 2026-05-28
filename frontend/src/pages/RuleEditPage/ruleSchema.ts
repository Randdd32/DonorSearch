import { z } from 'zod';

export const ruleSchema = z.object({
  ruleCode: z.string()
    .min(1, 'Код правила обязателен')
    .max(100, 'Код правила не должен превышать 100 символов'),
    
  ruleName: z.string()
    .min(1, 'Название правила обязательно')
    .max(200, 'Название правила не должно превышать 200 символов'),
    
  targetTypes: z.array(z.string()).min(1, 'Выберите минимум один целевой тип оборудования'),
  
  errorMessage: z.string().min(1, 'Сообщение об ошибке обязательно'),
  
  description: z.string().optional(),
  
  isActive: z.boolean(),
  
  expression: z.string().min(1, 'Выражение SpEL не может быть пустым')
});

export type RuleFormValues = z.infer<typeof ruleSchema>;