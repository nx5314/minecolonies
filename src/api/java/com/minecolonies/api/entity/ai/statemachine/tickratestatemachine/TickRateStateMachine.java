package com.minecolonies.api.entity.ai.statemachine.tickratestatemachine;

import com.minecolonies.api.entity.ai.statemachine.basestatemachine.BasicStateMachine;
import com.minecolonies.api.entity.ai.statemachine.states.AIBlockingEventType;
import com.minecolonies.api.entity.ai.statemachine.states.IState;
import org.jetbrains.annotations.NotNull;

import java.beans.EventHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

/**
 * Statemachine with an added tickrate limiting of transitions, allowing transitions to be checked at a lower rate. Default tickrate is 20 tps (Minecraft default).
 */
public class TickRateStateMachine<S extends IState> extends BasicStateMachine<ITickingTransition<S>, S> implements ITickRateStateMachine<S>
{
    /**
     * TPS factor of the server
     */
    public static double slownessFactor = 1.0D;

    /**
     * Counter keeping track of ticks
     */
    private int tickCounter = 0;

    /**
     * The rate the statemachine currently ticks at. Sets the amount of ticks - 1 which are skipped.
     */
    private int tickRate = 1;

    /**
     * The counter for the statemachine's tickrate.
     */
    private int tickRateCounter = 0;

    /**
     * Currently used transition
     */
    private ITickingTransition<S> executedTransition = null;

    /**
     * Reference to our used global transition lists
     */
    private final List<ITickingTransition<S>> aiBlockingTransitions;
    private final List<ITickingTransition<S>> stateBlockingTransitions;
    private final List<ITickingTransition<S>> eventTransitions;

    /**
     * Construct a new StateMachine
     *
     * @param exceptionHandler the exception handler.
     * @param initialState     the initial state.
     */
    public TickRateStateMachine(@NotNull final S initialState, @NotNull final Consumer<RuntimeException> exceptionHandler)
    {
        super(initialState, exceptionHandler);

        // Initial Lists
        aiBlockingTransitions = new ArrayList<>();
        this.eventTransitionMap.put(AIBlockingEventType.AI_BLOCKING, aiBlockingTransitions);
        stateBlockingTransitions = new ArrayList<>();
        this.eventTransitionMap.put(AIBlockingEventType.STATE_BLOCKING, stateBlockingTransitions);
        eventTransitions = new ArrayList<>();
        this.eventTransitionMap.put(AIBlockingEventType.EVENT, eventTransitions);
    }

    /**
     * Tick the statemachine.
     */
    @Override
    public void tick()
    {
        if (tickRateCounter > 1)
        {
            tickRateCounter--;
            return;
        }
        tickRateCounter = tickRate;

        for (int i = 0, aiBlockingTransitionsSize = aiBlockingTransitions.size(); i < aiBlockingTransitionsSize; i++)
        {
            if (checkTransition(aiBlockingTransitions.get(i)))
            {
                return;
            }
        }

        for (int i = 0, eventTransitionsSize = eventTransitions.size(); i < eventTransitionsSize; i++)
        {
            if (checkTransition(eventTransitions.get(i)))
            {
                return;
            }
        }

        for (int i = 0, stateBlockingTransitionsSize = stateBlockingTransitions.size(); i < stateBlockingTransitionsSize; i++)
        {
            if (checkTransition(stateBlockingTransitions.get(i)))
            {
                return;
            }
        }

        for (int i = 0, currentStateTransitionsSize = currentStateTransitions.size(); i < currentStateTransitionsSize; i++)
        {
            if (checkTransition(currentStateTransitions.get(i)))
            {
                return;
            }
        }
    }

    /**
     * Check the condition for a transition
     *
     * @param transition the target to check
     * @return true if this target worked and we should stop executing this tick
     */
    @Override
    public boolean checkTransition(@NotNull final ITickingTransition<S> transition)
    {
        // Check if the target should be run this Tick
        if (transition.countdownTicksToUpdate() > 0)
        {
            return false;
        }

        transition.setTicksToUpdate((int) (transition.getTickRate() / slownessFactor));
        executedTransition = transition;
        return super.checkTransition(transition);
    }

    @Override
    public int getTickRate()
    {
        return tickRate;
    }

    @Override
    public void setTickRate(final int tickRate)
    {
        this.tickRate = tickRate;
        tickRateCounter = new Random().nextInt(tickRate);
    }

    @Override
    public void setCurrentDelay(final int ticksToNext)
    {
        executedTransition.setTicksToUpdate(ticksToNext);
    }
}
