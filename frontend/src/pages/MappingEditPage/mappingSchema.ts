import { z } from 'zod';

export const mappingSchema = z.object({
  externalName: z.string()
    .min(1, 'Внешнее название обязательно для заполнения'),
    
  componentType: z.string().min(1, 'Выберите категорию комплектующего'),
  
  componentId: z.number()
    .optional()
    .refine((val) => val !== undefined, { message: 'Необходимо выбрать деталь из базы' }),
  
  componentName: z.string().optional()
});

export type MappingFormValues = z.infer<typeof mappingSchema>;