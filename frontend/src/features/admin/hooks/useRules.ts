import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { rulesService, type GetRulesParams } from '../../../services/rules.service';
import toast from 'react-hot-toast';

export const useRules = (params: GetRulesParams) => {
  return useQuery({
    queryKey: ['rules', params],
    queryFn: () => rulesService.getRules(params),
    placeholderData: (prev) => prev,
  });
};

export const useRuleMutations = () => {
  const queryClient = useQueryClient();
  const invalidate = () => queryClient.invalidateQueries({ queryKey: ['rules'] });

  const deleteMutation = useMutation({
    mutationFn: (id: number) => rulesService.delete(id),
    onSuccess: () => {
      toast.success('Правило удалено');
      invalidate();
    }
  });

  return { deleteMutation };
};