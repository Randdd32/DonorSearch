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
      const backendMsg = e instanceof AxiosError ? (e.response?.data?.message || e.message) : e.message;
      toast.error(
        `К сожалению, правило не удалось корректно обработать.\n\nТекст ошибки:\n${backendMsg}`, 
        { duration: 8000 }
      );
    }
  });
};