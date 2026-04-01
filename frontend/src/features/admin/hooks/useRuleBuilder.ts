import { useQuery, useMutation } from '@tanstack/react-query';
import { AxiosError } from 'axios'; 
import { rulesService } from '../../../services/rules.service';
import toast from 'react-hot-toast';

export const useRuleMetadata = () => {
  return useQuery({
    queryKey:['rule-metadata'],
    queryFn: () => rulesService.getBuilderMetadata(),
    staleTime: Infinity
  });
};

export const useValidateExpression = () => {
  return useMutation({
    mutationFn: (expression: string) => rulesService.validateExpression(expression),
    onSuccess: () => {
      toast.success('Синтаксис выражения корректен!', { icon: '✅' });
    },
    onError: (e: Error | AxiosError<{ message: string }>) => {
      if (e instanceof AxiosError) {
        toast.error(e.response?.data?.message || 'Ошибка синтаксиса SpEL (сервер)', { duration: 5000 });
      } else {
        toast.error(e.message || 'Неизвестная ошибка', { duration: 5000 });
      }
    }
  });
};