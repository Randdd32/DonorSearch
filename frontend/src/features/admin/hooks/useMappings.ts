import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { mappingsService, type GetMappingsParams } from '../../../services/mappings.service';
import toast from 'react-hot-toast';

export const useMappings = (params: GetMappingsParams) => {
  return useQuery({
    queryKey: ['mappings', params],
    queryFn: () => mappingsService.getMappings(params),
    placeholderData: (prev) => prev,
  });
};

export const useMappingMutations = () => {
  const queryClient = useQueryClient();

  const invalidate = () => queryClient.invalidateQueries({ queryKey: ['mappings'] });

  const confirmMutation = useMutation({
    mutationFn: ({ id, componentId }: { id: number, componentId: number }) => 
      mappingsService.update(id, { internalComponentId: componentId, confidence: 'CONFIRMED' }),
    onSuccess: () => {
      toast.success('Связь успешно подтверждена');
      invalidate();
    }
  });

  const deleteMutation = useMutation({
    mutationFn: (id: number) => mappingsService.delete(id),
    onSuccess: () => {
      toast.success('Запись удалена');
      invalidate();
    }
  });

  return { confirmMutation, deleteMutation };
};